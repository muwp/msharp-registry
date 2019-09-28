package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.data.mapper.MessageQueueMapper;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.MessageQueueDO;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.service.impl.RegistryServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    private volatile LinkedBlockingQueue<RegistryNodeDO> registryQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private volatile LinkedBlockingQueue<RegistryNodeDO> removeQueue = new LinkedBlockingQueue<RegistryNodeDO>();

    private volatile boolean executorStop = false;

    private ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    public void addRegistryNode(List<RegistryNodeDO> registryNodeList) {
        registryQueue.addAll(registryNodeList);
    }

    public void addRegistryNode(RegistryNodeDO registryNodeDO) {
        registryQueue.add(registryNodeDO);
    }

    public void removeRegistryNode(List<RegistryNodeDO> registryNodeList) {
        removeQueue.addAll(registryNodeList);
    }

    public void removeRegistryNode(RegistryNodeDO registryNodeDO) {
        removeQueue.add(registryNodeDO);
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
                if (registryNode != null) {
                    // refresh or add
                    int ret = registryNodeMapper.refresh(registryNode);
                    if (ret == 0) {
                        registryNodeMapper.add(registryNode);
                        sendRegistryDataUpdateMessage(registryNode);
                    }
                    syncUpdateRegistry(registryNode);
                }
            } catch (Exception e) {
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
                final RegistryNodeDO registryNode = removeQueue.take();
                if (registryNode != null) {
                    // delete
                    final int size = registryNodeMapper.deleteDataValue(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey(), registryNode.getValue());
                    if (size > 0) {
                        sendRegistryDataUpdateMessage(registryNode);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * update Registry And Message
     */
    private void syncUpdateRegistry(RegistryNodeDO registryNode) {
        // update registry and message
        RegistryDO registryDO = registryMapper.load(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
        if (registryDO == null) {
            registryDO = new RegistryDO();
            registryDO.setEnv(registryNode.getEnv());
            registryDO.setBiz(registryNode.getBiz());
            registryDO.setKey(registryNode.getKey());
            registryDO.setData(StringUtils.EMPTY);
            registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            registryMapper.add(registryDO);
        }
    }

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(RegistryNodeDO registryNodeDO) {
        final MessageQueueDO queueDO = new MessageQueueDO();
        queueDO.setBiz(registryNodeDO.getBiz());
        queueDO.setEnv(registryNodeDO.getEnv());
        queueDO.setKey(registryNodeDO.getKey());
        try {
            final List<MessageQueueDO> list = this.messageQueueMapper.queryForList(registryNodeDO.getBiz(), registryNodeDO.getEnv(), registryNodeDO.getKey());
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