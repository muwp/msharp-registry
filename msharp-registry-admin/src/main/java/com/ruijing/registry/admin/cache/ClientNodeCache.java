package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.executor.AsyncExecutor;
import com.ruijing.registry.admin.data.mapper.ClientNodeMapper;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.data.query.ClientNodeQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private com.google.common.cache.Cache<Pair<String, String>, List<ClientNodeDO>> clientNodeCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService clientNodeUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("client-node-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientNodeUpdateExecutor.scheduleWithFixedDelay(this::updateClientNode, 1, 5, TimeUnit.MINUTES);
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

    public int asyncRefreshNode(final ClientNodeDO clientNode) {
        AsyncExecutor.asyncExecute(() -> this.refreshNode(clientNode));
        return 1;
    }

    public int refreshNode(final ClientNodeDO clientNode) {
        List<ClientNodeDO> clientNodeList = clientNodeCache.getIfPresent(Pair.of(clientNode.getServiceName(), clientNode.getEnv()));
        ClientNodeDO targetNode = null;
        if (null != clientNodeList && clientNodeList.contains(clientNode)) {
            for (int i = 0, size = clientNodeList.size(); i < size; i++) {
                final ClientNodeDO nodeDO = clientNodeList.get(i);
                if (nodeDO.getServiceName().equalsIgnoreCase(clientNode.getServiceName()) && nodeDO.getEnv().equalsIgnoreCase(clientNode.getEnv()) && nodeDO.getClientAppkey().equalsIgnoreCase(clientNode.getClientAppkey())) {
                    targetNode = nodeDO;
                    Date nodeUpdateDate = nodeDO.getUpdateTime();
                    nodeDO.setUpdateTime(targetNode.getUpdateTime());
                    if (clientNode.getUpdateTime().getTime() - nodeUpdateDate.getTime() < ONE_HOUR) {
                        return 1;
                    } else {
                        break;
                    }
                }
            }
        }

        if (targetNode != null) {
            clientNode.setId(targetNode.getId());
        }

        int updateSize = 0;
        if (clientNode.getId() != null) {
            Transaction transaction = Cat.newTransaction("clientNodeMapper", "clientNodeMapper.updateByPrimaryKeySelective");
            try {
                updateSize = clientNodeMapper.updateByPrimaryKeySelective(clientNode);
                transaction.setSuccessStatus();
            } catch (Exception ex) {
                transaction.setStatus(ex);
            } finally {
                transaction.complete();
            }
        } else {
            updateSize = saveOrUpdate(clientNode);
        }
        return updateSize;
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
}