package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.registry.admin.data.mapper.ClientNodeMapper;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.data.query.ClientNodeQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * ClientNodeCache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class ClientNodeCache implements InitializingBean {

    private static final long ONE_HOUR = 3600000;

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 80;

    @Resource
    private ClientNodeMapper clientNodeMapper;

    private volatile boolean executorStop = false;

    private com.google.common.cache.Cache<Pair<String, String>, List<ClientNodeDO>> clientNodeCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService clientNodeUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("client-node-sync-update-thread", true));

    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));

    private volatile LinkedBlockingQueue<ClientNodeDO> clientNodeQueue = new LinkedBlockingQueue<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService.execute(this::scheduledSaveOrUpdateClientNode);
        this.clientNodeUpdateExecutor.scheduleWithFixedDelay(this::updateClientNode, 0, 5, TimeUnit.MINUTES);
    }

    public List<ClientNodeDO> get(final String serviceName, String env) {
        final Pair<String, String> pair = Pair.of(serviceName, env);
        List<ClientNodeDO> clientNodeList = clientNodeCache.getIfPresent(pair);
        if (CollectionUtils.isEmpty(clientNodeList)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            clientNodeList = syncGet(pair);
        }
        return clientNodeList;
    }

    public boolean refreshNode(final ClientNodeDO clientNode) {
        if (null == clientNode) {
            return false;
        }
        return clientNodeQueue.add(clientNode);
    }

    private void updateClientNode() {
        try {
            final Map<Pair<String, String>, List<ClientNodeDO>> clientMap = new HashMap<>();
            int index = 1;
            boolean stop = false;
            while (!stop) {
                ClientNodeQuery query = new ClientNodeQuery();
                query.setPageSize(DEFAULT_BATCH_UPDATE_SIZE);
                query.setOffset((index++ - 1L) * DEFAULT_BATCH_UPDATE_SIZE);
                final List<ClientNodeDO> clientNodeList = clientNodeMapper.queryForList(query);
                if (CollectionUtils.isEmpty(clientNodeList)) {
                    break;
                }

                for (int i = 0, size = clientNodeList.size(); i < size; i++) {
                    final ClientNodeDO clientNode = clientNodeList.get(i);
                    final Pair<String, String> entry = Pair.of(clientNode.getServiceName(), clientNode.getEnv());
                    List<ClientNodeDO> nodeList = clientMap.get(entry);
                    if (CollectionUtils.isEmpty(nodeList)) {
                        nodeList = new ArrayList<>();
                        clientMap.put(entry, nodeList);
                    }
                    nodeList.add(clientNode);
                }

                if (clientNodeList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                    stop = true;
                }
            }

            for (Map.Entry<Pair<String, String>, List<ClientNodeDO>> entry : clientMap.entrySet()) {
                clientNodeCache.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception ex) {
            Cat.logError("ClientNodeCache", "updateClientNode", null, ex);
        }
    }

    private List<ClientNodeDO> syncGet(final Pair<String, String> key) {
        return clientNodeMapper.query(key.getKey(), key.getRight());
    }

    private void scheduledSaveOrUpdateClientNode() {
        while (!executorStop) {
            try {
                final ClientNodeDO clientNode = clientNodeQueue.take();
                if (null == clientNode) {
                    continue;
                }
                final List<ClientNodeDO> clientNodeList = clientNodeCache.getIfPresent(Pair.of(clientNode.getServiceName(), clientNode.getEnv()));
                ClientNodeDO targetNode = this.getClientNode(clientNode.getServiceName(), clientNode.getEnv(), clientNode.getClientAppkey(), clientNodeList);
                boolean canExecute = true;
                if (targetNode != null) {
                    Date nodeUpdateDate = targetNode.getUpdateTime();
                    targetNode.setUpdateTime(clientNode.getUpdateTime());
                    if (clientNode.getUpdateTime().getTime() - nodeUpdateDate.getTime() < ONE_HOUR) {
                        canExecute = false;
                    }
                }

                if (!canExecute) {
                    continue;
                }

                if (targetNode != null) {
                    clientNode.setId(targetNode.getId());
                }

                if (clientNode.getId() != null) {
                    update(clientNode);
                } else {
                    saveOrUpdate(clientNode);
                }
            } catch (Exception e) {
                Cat.logError("ClientNodeCache", "scheduledSaveOrUpdateClientNode", StringUtils.EMPTY, e);
            }
        }
    }

    private ClientNodeDO getClientNode(String serviceName, String env, String clientAppkey, List<ClientNodeDO> clientNodeList) {
        if (CollectionUtils.isEmpty(clientNodeList)) {
            return null;
        }
        for (int i = 0, size = clientNodeList.size(); i < size; i++) {
            final ClientNodeDO nodeDO = clientNodeList.get(i);
            if (nodeDO.getServiceName().equalsIgnoreCase(serviceName) && nodeDO.getEnv().equalsIgnoreCase(env) && nodeDO.getClientAppkey().equalsIgnoreCase(clientAppkey)) {
                return nodeDO;
            }
        }
        return null;
    }

    private void update(final ClientNodeDO nodeDO) {
        Transaction transaction = Cat.newTransaction("clientNodeMapper", "clientNodeMapper.updateByPrimaryKeySelective");
        try {
            clientNodeMapper.updateByPrimaryKeySelective(nodeDO);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
    }

    private int saveOrUpdate(ClientNodeDO nodeDO) {
        int updateSize = 0;
        Transaction transaction = Cat.newTransaction("clientNodeMapper", "clientNodeMapper.insertSelective");
        try {
            updateSize = clientNodeMapper.refresh(nodeDO);
            if (updateSize <= 0) {
                updateSize = clientNodeMapper.insertSelective(nodeDO);
            }
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }

        return updateSize;
    }
}