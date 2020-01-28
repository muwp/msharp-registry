package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.data.mapper.ClientNodeMapper;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.data.query.ClientNodeQuery;
import com.ruijing.registry.admin.enums.RegistryClientNodeStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
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

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 80;

    @Resource
    private ClientNodeMapper clientNodeMapper;

    private Cache<Triple<String, String, String>, List<ClientNodeDO>> clientNodeCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService clientNodeUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("client-node-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientNodeUpdateExecutor.scheduleWithFixedDelay(this::scheduleUpdateClientNode, 0, 5, TimeUnit.MINUTES);
    }

    public List<ClientNodeDO> get(String appkey, String env, final String serviceName) {
        final Triple<String, String, String> pair = Triple.of(appkey, env, serviceName);
        final List<ClientNodeDO> clientNodeList = clientNodeCache.getIfPresent(pair);
        return clientNodeList;
    }

    public List<ClientNodeDO> syncGet(String appkey, String env, final String serviceName) {
        final Triple<String, String, String> triple = Triple.of(appkey, env, serviceName);
        List<ClientNodeDO> clientNodeList = clientNodeCache.getIfPresent(triple);
        if (CollectionUtils.isEmpty(clientNodeList)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            clientNodeList = syncGet(triple);
        }
        return clientNodeList;
    }

    private void scheduleUpdateClientNode() {
        try {
            final Map<Triple<String, String, String>, List<ClientNodeDO>> clientMap = new HashMap<>();
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
                    if (null == clientNode.getStatus() || clientNode.getStatus() == RegistryClientNodeStatusEnum.DELETED.getCode()) {
                        continue;
                    }
                    final Triple<String, String, String> triple = Triple.of(clientNode.getAppkey(), clientNode.getEnv(), clientNode.getServiceName());
                    List<ClientNodeDO> nodeList = clientMap.get(triple);
                    if (CollectionUtils.isEmpty(nodeList)) {
                        nodeList = new ArrayList<>();
                        clientMap.put(triple, nodeList);
                    }
                    nodeList.add(clientNode);
                }

                if (clientNodeList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                    stop = true;
                }
            }

            for (Map.Entry<Triple<String, String, String>, List<ClientNodeDO>> entry : clientMap.entrySet()) {
                clientNodeCache.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception ex) {
            Cat.logError("ClientNodeCache", "updateClientNode", null, ex);
        }
    }

    private List<ClientNodeDO> syncGet(final Triple<String, String, String> key) {
        final ClientNodeQuery query = new ClientNodeQuery();
        query.setAppkey(key.getLeft());
        query.setEnv(key.getMiddle());
        query.setServiceName(key.getRight());
        List<ClientNodeDO> list = clientNodeMapper.queryForList(query);
        return filter(list);
    }

    private List<ClientNodeDO> filter(List<ClientNodeDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        final List<ClientNodeDO> result = new ArrayList<>();
        for (int i = 0, size = list.size(); i < size; i++) {
            final ClientNodeDO clientNode = list.get(i);
            if (null == clientNode.getStatus() || clientNode.getStatus() == RegistryClientNodeStatusEnum.DELETED.getCode()) {
                continue;
            }
            result.add(clientNode);
        }
        return result;
    }
}