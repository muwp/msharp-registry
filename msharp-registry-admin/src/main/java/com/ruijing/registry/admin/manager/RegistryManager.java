package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.data.mapper.MessageQueueMapper;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.MessageQueueDO;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.service.impl.RegistryServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * RegistryManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryManager implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(RegistryServiceImpl.class);

    @Resource
    private RegistryMapper registryMapper;

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    @Resource
    private MessageQueueMapper messageQueueMapper;

    @Autowired
    private RegistryCache registryCache;

    @Autowired
    private RegistryNodeCache registryNodeCache;

    private volatile LinkedBlockingQueue<RegistryNodeDO> registryQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private volatile LinkedBlockingQueue<RegistryNodeDO> removeQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private volatile boolean executorStop = false;

    private ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    public void addRegistryNodeList(final List<RegistryNodeDO> registryNodeList) {
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return;
        }
        this.registryQueue.addAll(registryNodeList);
    }

    public void addRegistryNode(RegistryNodeDO registryNode) {
        if (null == registryNode) {
            return;
        }
        registryQueue.add(registryNode);
    }

    public void removeRegistryNodeList(List<RegistryNodeDO> registryNodeList) {
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return;
        }
        removeQueue.addAll(registryNodeList);
    }

    public void removeRegistryNode(RegistryNodeDO registryNode) {
        if (null == registryNode) {
            return;
        }
        removeQueue.add(registryNode);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService.execute(this::scheduledSaveOrUpdateRegistryNode);
        this.executorService.execute(this::scheduledClearRegistryNode);
    }

    /**
     * registry registry data
     */
    private void scheduledSaveOrUpdateRegistryNode() {
        while (!executorStop) {
            try {
                RegistryNodeDO registryNode = registryQueue.take();
                if (null == registryNode) {
                    continue;
                }
                // refresh or add
                final Long nodeId = this.syncUpdateRegistry(registryNode);
                final RegistryNodeDO node = new RegistryNodeDO();
                if (null != nodeId) {
                    node.setId(nodeId);
                } else {
                    node.setBiz(registryNode.getBiz());
                    node.setEnv(registryNode.getEnv());
                    node.setKey(registryNode.getKey());
                    node.setValue(registryNode.getValue());
                }
                final int updateSize = registryNodeMapper.refresh(node);
                if (updateSize == 0) {
                    final RegistryDO registryDO = registryCache.get(Triple.of(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey()));
                    registryNode.setRegistryId(registryDO.getId());
                    registryNodeMapper.add(registryNode);
                    this.sendMessageQueue(registryNode);
                }
            } catch (Exception e) {
                Cat.logError("RegistryManager", "scheduledSaveOrUpdateRegistryNode", StringUtils.EMPTY, e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * remove registry data (client-num/start-interval s)
     */
    private void scheduledClearRegistryNode() {
        while (!executorStop) {
            try {
                final RegistryNodeDO registryNode = this.removeQueue.take();
                if (null == registryNode) {
                    continue;
                }
                // delete
                final int size = registryNodeMapper.deleteDataValue(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey(), registryNode.getValue());
                if (size > 0) {
                    this.sendMessageQueue(registryNode);
                }
            } catch (Exception e) {
                Cat.logError("RegistryManager", "scheduledClearRegistryNode", StringUtils.EMPTY, e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * add Registry
     */
    private Long syncUpdateRegistry(final RegistryNodeDO registryNode) {
        final Triple<String, String, String> triple = Triple.of(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
        RegistryDO registryDO = this.registryCache.get(triple);
        if (null == registryDO) {
            // update registry and message
            registryDO = new RegistryDO();
            registryDO.setEnv(registryNode.getEnv());
            registryDO.setBiz(registryNode.getBiz());
            registryDO.setKey(registryNode.getKey());
            registryDO.setData(StringUtils.EMPTY);
            registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            try {
                this.registryMapper.add(registryDO);
            } catch (Exception ex) {
                //volatile
            }
        }

        final List<RegistryNodeDO> registryNodeDOList = this.registryNodeCache.get(triple);
        if (CollectionUtils.isEmpty(registryNodeDOList)) {
            return null;
        }
        for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
            final RegistryNodeDO tmp = registryNodeDOList.get(i);
            if (registryNode.getValue().trim().equals(tmp.getValue())) {
                return tmp.getId();
            }
        }
        return null;
    }

    /**
     * send RegistryData Update Message
     */
    private void sendMessageQueue(RegistryNodeDO registryNode) {
        final MessageQueueDO queueDO = new MessageQueueDO();
        queueDO.setBiz(registryNode.getBiz());
        queueDO.setEnv(registryNode.getEnv());
        queueDO.setKey(registryNode.getKey());
        try {
            final List<MessageQueueDO> list = this.messageQueueMapper.queryForList(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
            if (CollectionUtils.isEmpty(list)) {
                final Date date = new Date();
                queueDO.setUpdateTime(date);
                queueDO.setSequenceId(System.currentTimeMillis());
                try {
                    this.messageQueueMapper.insertSelective(queueDO);
                } catch (Exception ex) {
                    Cat.logError("RegistryManager", "insertSelective", null, ex);
                }
            } else {
                final MessageQueueDO messageQueueDO = new MessageQueueDO();
                final Date date = new Date();
                messageQueueDO.setUpdateTime(date);
                messageQueueDO.setSequenceId(System.currentTimeMillis());
                messageQueueDO.setId(list.get(0).getId());
                this.messageQueueMapper.updateByPrimaryKeySelective(messageQueueDO);
            }
        } catch (Exception e) {
            Cat.logError("RegistryManager", "syncMessageQueue", null, e);
        }
    }
}