package com.ruijing.registry.admin.manager;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.data.mapper.MessageQueueMapper;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.MessageQueueDO;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
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
                final Long nodeId = this.syncUpdateRegistryAndReturnNodeId(registryNode);
                final RegistryNodeDO node = new RegistryNodeDO();
                if (null != nodeId) {
                    node.setId(nodeId);
                } else {
                    node.setBiz(registryNode.getBiz());
                    node.setEnv(registryNode.getEnv());
                    node.setKey(registryNode.getKey());
                    node.setValue(registryNode.getValue());
                }

                int updateSize = 0;
                Transaction transaction = Cat.newTransaction("registryManager", "registryNodeMapper.refresh");
                try {
                    updateSize = registryNodeMapper.refresh(node);
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                } finally {
                    transaction.complete();
                }

                if (updateSize == 0) {
                    boolean isUpdate = true;
                    final RegistryDO registryDO = registryCache.get(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey());
                    registryNode.setRegistryId(registryDO.getId());
                    Transaction newTransaction = Cat.newTransaction("registryManager", "registryNodeMapper.add");
                    try {
                        registryNodeMapper.add(registryNode);
                        newTransaction.setSuccessStatus();
                    } catch (Exception ex) {
                        if (ex instanceof MySQLIntegrityConstraintViolationException) {
                            isUpdate = false;
                            Cat.logError("registryManager", "registryNodeMapper.add", null, ex);
                        }
                        newTransaction.setStatus(ex);
                    } finally {
                        newTransaction.complete();
                    }

                    if (isUpdate) {
                        this.sendMessageQueue(registryNode);
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
                int deletedSize = 0;
                Transaction transaction = Cat.newTransaction("registryManager", "registryNodeMapper.deleteDataValue");
                try {
                    deletedSize = registryNodeMapper.deleteDataValue(registryNode.getBiz(), registryNode.getEnv(), registryNode.getKey(), registryNode.getValue());
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                } finally {
                    transaction.complete();
                }
                if (deletedSize > 0) {
                    this.sendMessageQueue(registryNode);
                }
            } catch (Exception e) {
                Cat.logError("RegistryManager", "scheduledClearRegistryNode", StringUtils.EMPTY, e);
            }
        }
    }

    /**
     * add Registry
     */
    private Long syncUpdateRegistryAndReturnNodeId(final RegistryNodeDO registryNode) {
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
            Transaction transaction = Cat.newTransaction("registryManager", "registryMapper.add");
            try {
                this.registryMapper.add(registryDO);
                transaction.setSuccessStatus();
            } catch (Exception ex) {
                transaction.setStatus(ex);
            } finally {
                transaction.complete();
            }
        }

        final List<RegistryNodeDO> registryNodeDOList = this.registryNodeCache.get(triple);
        if (CollectionUtils.isEmpty(registryNodeDOList)) {
            return null;
        }

        for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
            final RegistryNodeDO tmp = registryNodeDOList.get(i);
            if (Objects.equals(registryNode.getValue(), tmp.getValue())) {
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
                Transaction transaction = Cat.newTransaction("registryManager", "messageQueueMapper.insertSelective");
                try {
                    this.messageQueueMapper.insertSelective(queueDO);
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                } finally {
                    transaction.complete();
                }
            } else {
                final MessageQueueDO messageQueueDO = new MessageQueueDO();
                final Date date = new Date();
                messageQueueDO.setUpdateTime(date);
                messageQueueDO.setSequenceId(System.currentTimeMillis());
                messageQueueDO.setId(list.get(0).getId());
                Transaction transaction = Cat.newTransaction("registryManager", "messageQueueMapper.updateByPrimaryKeySelective");
                try {
                    this.messageQueueMapper.updateByPrimaryKeySelective(messageQueueDO);
                    transaction.setSuccessStatus();
                } catch (Exception ex) {
                    transaction.setStatus(ex);
                } finally {
                    transaction.complete();
                }
            }
        } catch (Exception e) {
            Cat.logError("RegistryManager", "syncMessageQueue", null, e);
        }
    }
}