package com.ruijing.registry.admin.cache;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.threadpool.NamedThreadFactory;
import com.ruijing.registry.admin.data.mapper.RegistryMapper;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.query.RegistryQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RegistryCache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryCache implements InitializingBean {

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 100;

    @Resource
    private RegistryMapper registryMapper;

    private volatile Map<Triple<String, String, String>, RegistryDO> registryCache = new ConcurrentHashMap<>();

    private volatile Map<Long, RegistryDO> registryIdCache = new ConcurrentHashMap<>();

    private volatile Set<Pair<Long, Triple<String, String, String>>> registryIdSet = new ConcurrentHashSet<>();

    public RegistryDO get(final Triple<String, String, String> key) {
        RegistryDO registryDO = registryCache.get(key);
        if (null == registryDO) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryDO = syncGet(key);
        }
        return registryDO;
    }

    public RegistryDO get(final Long id) {
        RegistryDO registryDO = registryIdCache.get(id);
        if (null == registryDO) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryDO = syncGet(id);
        }
        return registryDO;
    }

    public void put(final Triple<String, String, String> key, RegistryDO registryDO) {
        registryCache.put(key, registryDO);
    }

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService registryUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registryUpdateExecutor.scheduleWithFixedDelay(this::updateRegistry, 1, 4, TimeUnit.SECONDS);
    }

    private void updateRegistry() {
        try {
            final Set<Pair<Long, Triple<String, String, String>>> registryIdSet = new HashSet<>();
            int index = 1;
            boolean stop = false;
            while (!stop) {
                final RegistryQuery query = new RegistryQuery();
                query.setOffset((index++ - 1L) * DEFAULT_BATCH_UPDATE_SIZE);
                query.setPageSize(DEFAULT_BATCH_UPDATE_SIZE);
                final List<RegistryDO> registryList = registryMapper.queryForList(query);
                if (CollectionUtils.isEmpty(registryList)) {
                    break;
                }

                for (int i = 0, size = registryList.size(); i < size; i++) {
                    final RegistryDO registryDO = registryList.get(i);
                    this.registryCache.put(Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey()), registryDO);
                    this.registryIdCache.put(registryDO.getId(), registryDO);
                    registryIdSet.add(Pair.of(registryDO.getId(), Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey())));
                }
                if (registryList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                    stop = true;
                }
            }
            this.setRegistrySet(registryIdSet);
        } catch (Exception ex) {
            Cat.logError("RegistryCache", "updateRegistry", null, ex);
        }
    }

    private RegistryDO syncGet(final Triple<String, String, String> key) {
        return registryMapper.load(key.getLeft(), key.getMiddle(), key.getRight());
    }

    private RegistryDO syncGet(final Long id) {
        return registryMapper.loadById(id);
    }

    public synchronized Set<Pair<Long, Triple<String, String, String>>> getRegistrySet() {
        return new HashSet<>(this.registryIdSet);
    }

    private synchronized void setRegistrySet(Set<Pair<Long, Triple<String, String, String>>> registryIdSet) {
        this.registryIdSet.clear();
        this.registryIdSet.addAll(registryIdSet);
    }
}