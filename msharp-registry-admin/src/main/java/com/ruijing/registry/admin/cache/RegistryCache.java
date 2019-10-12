package com.ruijing.registry.admin.cache;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
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
import java.util.*;
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
public class RegistryCache implements Cache<RegistryDO>, InitializingBean {

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 100;

    @Resource
    private RegistryMapper registryMapper;

    private com.google.common.cache.Cache<Triple<String, String, String>, RegistryDO> registryCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    private com.google.common.cache.Cache<Long, RegistryDO> registryIdCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    private volatile Set<Pair<Long, Triple<String, String, String>>> registryIdSet = new ConcurrentHashSet<>();

    @Override
    public RegistryDO get(final Triple<String, String, String> key) {
        RegistryDO registryDO = registryCache.getIfPresent(key);
        if (null == registryDO) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryDO = syncGet(key);
        }
        return registryDO;
    }

    @Override
    public RegistryDO get(String biz, String env, String key) {
        return get(Triple.of(biz, env, key));
    }

    @Override
    public RegistryDO get(final Long id) {
        RegistryDO registryDO = registryIdCache.getIfPresent(id);
        if (null == registryDO) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryDO = syncGet(id);
        }
        return registryDO;
    }

    @Override
    public void put(final Triple<String, String, String> key, RegistryDO registryDO) {
        this.registryCache.put(key, registryDO);
    }

    @Override
    public boolean remove(final Long id) {
        RegistryDO registryDO = registryIdCache.getIfPresent(id);
        if (null != registryDO) {
            registryIdCache.invalidate(id);
            registryCache.invalidate(Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey()));
            int size = registryMapper.delete(id);
            return size >= 1;
        }
        return false;
    }

    @Override
    public boolean remove(RegistryDO R) {
        Triple<String, String, String> key = Triple.of(R.getBiz(), R.getEnv(), R.getKey());
        RegistryDO registryDO = registryCache.getIfPresent(key);
        if (null != registryDO) {
            registryCache.invalidate(key);
            registryIdCache.invalidate(registryDO.getId());
            int size = registryMapper.delete(registryDO.getId());
            return size >= 1;
        }
        return false;
    }

    @Override
    public int refresh(RegistryDO registryDO) {
        this.registryCache.put(Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey()), registryDO);
        this.registryIdCache.put(registryDO.getId(), registryDO);
        return registryMapper.update(registryDO);
    }

    @Override
    public int persist(RegistryDO registryDO) {
        int updateSize = 0;
        Transaction transaction = Cat.newTransaction("registryManager", "registryMapper.add");
        try {
            updateSize = this.registryMapper.add(registryDO);
            registryIdCache.put(registryDO.getId(), registryDO);
            registryCache.put(Triple.of(registryDO.getBiz(), registryDO.getEnv(), registryDO.getKey()), registryDO);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return updateSize;
    }

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService registryUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registryUpdateExecutor.scheduleWithFixedDelay(this::updateRegistry, 1, 5, TimeUnit.SECONDS);
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

    public synchronized List<Pair<Long, Triple<String, String, String>>> getRegistryList() {
        return new ArrayList<>(this.registryIdSet);
    }

    private synchronized void setRegistrySet(Set<Pair<Long, Triple<String, String, String>>> registryIdSet) {
        this.registryIdSet.clear();
        this.registryIdSet.addAll(registryIdSet);
    }
}