package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RegistryNodeCache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryNodeCache implements InitializingBean {

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    @Resource
    private RegistryCache registryCache;

    private volatile Map<Triple<String, String, String>, List<RegistryNodeDO>> registryNodeCache = new ConcurrentHashMap<>();

    private volatile Map<Long, List<RegistryNodeDO>> registryIdNodeCache = new ConcurrentHashMap<>();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService nodeUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-node-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.nodeUpdateExecutor.scheduleWithFixedDelay(this::updateRegistryNode, 1, 2, TimeUnit.SECONDS);
    }

    public List<RegistryNodeDO> get(final Long registryId) {
        List<RegistryNodeDO> registryCache = registryIdNodeCache.get(registryId);
        if (CollectionUtils.isEmpty(registryCache)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryCache = syncGet(registryId);
        }
        return registryCache;
    }

    public List<RegistryNodeDO> get(final Triple<String, String, String> key) {
        List<RegistryNodeDO> registryCache = registryNodeCache.get(key);
        if (CollectionUtils.isEmpty(registryCache)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryCache = syncGet(key);
        }
        return registryCache;
    }

    private void updateRegistryNode() {
        try {
            final Set<Pair<Long, Triple<String, String, String>>> registryIdSet = registryCache.getRegistrySet();
            if (CollectionUtils.isEmpty(registryIdSet)) {
                return;
            }
            for (final Pair<Long, Triple<String, String, String>> pair : registryIdSet) {
                final List<RegistryNodeDO> registryNodeList = registryNodeMapper.findByRegistryId(pair.getKey());
                registryIdNodeCache.put(pair.getKey(), registryNodeList);
                registryNodeCache.put(pair.getRight(), registryNodeList);
            }
        } catch (Exception ex) {
            Cat.logError("RegistryNodeCache", "updateRegistryNode", null, ex);
        }
    }

    private List<RegistryNodeDO> syncGet(final Triple<String, String, String> key) {
        return registryNodeMapper.findData(key.getLeft(), key.getMiddle(), key.getRight());
    }

    private List<RegistryNodeDO> syncGet(final Long registryId) {
        return registryNodeMapper.findByRegistryId(registryId);
    }
}