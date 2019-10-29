package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.service.MessageQueueService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private MessageQueueService messageQueueService;

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
                final Pair<Long, Long> pairId = this.syncUpdateRegistryAndReturnNodeId(registryNode);
                final RegistryNodeDO node = new RegistryNodeDO();
                final Long nodeId = pairId.getKey();
                final Long registryId = pairId.getRight();
                if (null != nodeId) {
                    node.setId(nodeId);
                    node.setMeta(registryNode.getMeta());
                    node.setRegistryId(registryId);
                } else {
                    node.setAppkey(registryNode.getAppkey());
                    node.setEnv(registryNode.getEnv());
                    node.setServiceName(registryNode.getServiceName());
                    node.setValue(registryNode.getValue());
                    node.setRegistryId(registryId);
                    node.setMeta(registryNode.getMeta());
                }

                final int updateSize = registryNodeCache.refresh(Arrays.asList(node));

                if (updateSize == 0) {
                    final RegistryDO registryDO = registryCache.get(registryNode.getAppkey(), registryNode.getEnv(), registryNode.getServiceName());
                    registryNode.setRegistryId(registryDO.getId());
                    int isUpdate = registryNodeCache.persist(Arrays.asList(registryNode));
                    if (isUpdate > 0) {
                        this.messageQueueService.sendMessageQueue(registryNode);
                    }
                }
            } catch (Exception e) {
                Cat.logError("RegistryManager", "scheduledSaveOrUpdateRegistryNode", StringUtils.EMPTY, e);
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
                boolean deletedSize = registryNodeCache.remove(Arrays.asList(registryNode));
                if (deletedSize) {
                    this.messageQueueService.sendMessageQueue(registryNode);
                }
            } catch (Exception e) {
                Cat.logError("RegistryManager", "scheduledClearRegistryNode", StringUtils.EMPTY, e);
            }
        }
    }

    /**
     * add Registry
     */
    private Pair<Long, Long> syncUpdateRegistryAndReturnNodeId(final RegistryNodeDO registryNode) {
        final Triple<String, String, String> triple = Triple.of(registryNode.getAppkey(), registryNode.getEnv(), registryNode.getServiceName());
        RegistryDO registryDO = this.registryCache.get(triple);
        if (null == registryDO) {
            // update registry and message
            registryDO = new RegistryDO();
            registryDO.setEnv(registryNode.getEnv());
            registryDO.setAppkey(registryNode.getAppkey());
            registryDO.setServiceName(registryNode.getServiceName());
            registryDO.setData(StringUtils.EMPTY);
            registryDO.setStatus(0);
            registryDO.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            this.registryCache.persist(registryDO);
        }

        if (registryDO.getId() == null) {
            registryDO = this.registryCache.get(triple);
        }

        final List<RegistryNodeDO> registryNodeDOList = this.registryNodeCache.get(triple);
        if (CollectionUtils.isEmpty(registryNodeDOList)) {
            return Pair.of(null, registryDO.getId());
        }

        for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
            final RegistryNodeDO tmp = registryNodeDOList.get(i);
            if (Objects.equals(registryNode.getValue(), tmp.getValue())) {
                return Pair.of(tmp.getId(), registryDO.getId());
            }
        }
        return Pair.of(null, registryDO.getId());
    }
}