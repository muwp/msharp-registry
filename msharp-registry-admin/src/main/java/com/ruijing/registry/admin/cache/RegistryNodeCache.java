package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
public class RegistryNodeCache implements Cache<List<RegistryNodeDO>>, InitializingBean {

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 50;

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
        this.nodeUpdateExecutor.scheduleWithFixedDelay(this::scheduleUpdateRegistryNode, 1, 4, TimeUnit.SECONDS);
    }

    @Override
    public List<RegistryNodeDO> get(final Long registryId) {
        List<RegistryNodeDO> registryListCache = registryIdNodeCache.get(registryId);
        if (CollectionUtils.isEmpty(registryListCache)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryListCache = syncGet(registryId);
        }
        return registryListCache;
    }

    @Override
    public List<RegistryNodeDO> get(String appkey, String env, String serviceName) {
        return get(Triple.of(appkey, env, serviceName));
    }

    @Override
    public List<RegistryNodeDO> get(final Triple<String, String, String> key) {
        List<RegistryNodeDO> registryCache = registryNodeCache.get(key);
        if (CollectionUtils.isEmpty(registryCache)) {
            //如果miss hit则重新从db中获取一次，此事件发生的概率非常小
            registryCache = syncGet(key);
        }
        return registryCache;
    }

    @Override
    public boolean remove(final Long id) {
        final List<RegistryNodeDO> registryNodeList = registryIdNodeCache.get(id);
        if (null == registryNodeList) {
            return false;
        }

        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = registryNodeList.get(i);
            if (id.equals(registryNode.getId())) {
                return deleteNode(registryNode.getId()) > 0;
            }
        }
        return false;
    }

    @Override
    public boolean remove(final List<RegistryNodeDO> registryNodeList) {
        int del = 0;
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = registryNodeList.get(i);
            del += registryNode.getId() == null ? deleteNode(registryNode.getAppkey(), registryNode.getEnv(), registryNode.getServiceName(), registryNode.getValue()) : deleteNode(registryNode.getId());
        }
        return del > 0;
    }

    @Override
    public int refresh(List<RegistryNodeDO> registryNodeList) {
        int updateSize = 0;
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = registryNodeList.get(i);
            updateSize += refreshNode(registryNode);
        }
        return updateSize;
    }

    private int refreshNode(final RegistryNodeDO registryNode) {
        int updateSize = 0;
        Transaction transaction = Cat.newTransaction("registryManager", "registryNodeMapper.refresh");
        try {
            updateSize = registryNodeMapper.refresh(registryNode);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return updateSize;
    }

    @Override
    public int persist(final List<RegistryNodeDO> registryNodeList) {
        //persist
        int updateSize = 0;
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = registryNodeList.get(i);
            updateSize += addNode(registryNode);
        }
        return updateSize;
    }

    private int addNode(final RegistryNodeDO registryNode) {
        //update
        int updateSize = 0;
        Transaction newTransaction = Cat.newTransaction("registryManager", "registryNodeMapper.add");
        try {
            updateSize = registryNodeMapper.add(registryNode);
            newTransaction.setSuccessStatus();
        } catch (Exception ex) {
            if (ex instanceof MySQLIntegrityConstraintViolationException) {
                Cat.logError("registryManager", "registryNodeMapper.add", null, ex);
            }
            newTransaction.setStatus(ex);
        } finally {
            newTransaction.complete();
        }
        return updateSize;
    }

    private int deleteNode(final String biz, final String env, final String key, final String value) {
        // delete
        int deletedSize = 0;
        Transaction transaction = Cat.newTransaction("registryManager", "registryNodeMapper.removeDataValue");
        try {
            deletedSize = registryNodeMapper.removeDataValue(biz, env, key, value);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return deletedSize;
    }

    private int deleteNode(final Long id) {
        // delete
        int deletedSize = 0;
        final Transaction transaction = Cat.newTransaction("registryManager", "registryNodeMapper.delete");
        try {
            deletedSize = this.registryNodeMapper.remove(id);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
        return deletedSize;
    }

    private void scheduleUpdateRegistryNode() {
        try {
            final List<Pair<Long, Triple<String, String, String>>> registryCacheList = registryCache.getRegistryList();
            if (CollectionUtils.isEmpty(registryCacheList)) {
                return;
            }

            int index = 1;
            int size = registryCacheList.size();
            while (true) {
                int fromIndex = (index - 1) * DEFAULT_BATCH_UPDATE_SIZE;
                if (fromIndex >= size) {
                    break;
                }
                int toIndex = index * DEFAULT_BATCH_UPDATE_SIZE;
                toIndex = toIndex > size ? size : toIndex;
                final List<Pair<Long, Triple<String, String, String>>> subList = registryCacheList.subList(fromIndex, toIndex);
                index++;

                final List<Long> registryIdList = New.listWithCapacity(subList.size());
                for (int i = 0, len = subList.size(); i < len; i++) {
                    registryIdList.add(subList.get(i).getKey());
                }

                final List<RegistryNodeDO> registryNodeDOList = registryNodeMapper.findByRegistryIdList(registryIdList);

                final Map<Pair<Long, Triple<String, String, String>>, List<RegistryNodeDO>> map = toMap(registryNodeDOList);

                for (int i = 0, len = subList.size(); i < len; i++) {
                    final Pair<Long, Triple<String, String, String>> pair = subList.get(i);
                    final List<RegistryNodeDO> list = map.get(pair);
                    if (null == list) {
                        if (registryIdNodeCache.containsKey(pair.getKey())) {
                            this.registryIdNodeCache.remove(pair.getKey());
                            this.registryNodeCache.remove(pair.getRight());
                        }
                    } else {
                        this.registryIdNodeCache.put(pair.getKey(), list);
                        this.registryNodeCache.put(pair.getRight(), list);
                    }
                }

                if (subList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                    break;
                }
            }
        } catch (Exception ex) {
            Cat.logError("RegistryNodeCache", "updateRegistryNode", null, ex);
        }
    }

    private Map<Pair<Long, Triple<String, String, String>>, List<RegistryNodeDO>> toMap(final List<RegistryNodeDO> registryNodeList) {
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return Collections.emptyMap();
        }
        final Map<Pair<Long, Triple<String, String, String>>, List<RegistryNodeDO>> registryNodeCache = New.mapWithCapacity(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO registryNode = registryNodeList.get(i);
            if (registryNode.getStatus() == null || registryNode.getStatus() == RegistryNodeStatusEnum.DELETED.getCode()) {
                continue;
            }
            final Pair<Long, Triple<String, String, String>> pair = Pair.of(registryNode.getRegistryId(), Triple.of(registryNode.getAppkey(), registryNode.getEnv(), registryNode.getServiceName()));
            List<RegistryNodeDO> registryList = registryNodeCache.get(pair);
            if (null == registryList) {
                registryList = New.list();
                registryNodeCache.put(pair, registryList);
            }
            registryList.add(registryNode);
        }
        return registryNodeCache;
    }

    private List<RegistryNodeDO> syncGet(final Triple<String, String, String> key) {
        final List<RegistryNodeDO> registryNodeList = registryNodeMapper.findData(key.getLeft(), key.getMiddle(), key.getRight());
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return Collections.emptyList();
        }
        return filter(registryNodeList);
    }

    private List<RegistryNodeDO> syncGet(final Long registryId) {
        final List<RegistryNodeDO> registryNodeList = registryNodeMapper.findByRegistryId(registryId);
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return Collections.emptyList();
        }
        return filter(registryNodeList);
    }

    private List<RegistryNodeDO> filter(List<RegistryNodeDO> registryNodeList) {
        final List<RegistryNodeDO> nodeDOList = New.listWithCapacity(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO node = registryNodeList.get(i);
            if (node.getStatus() == null || node.getStatus() == RegistryNodeStatusEnum.DELETED.getCode()) {
                continue;
            }
            nodeDOList.add(node);
        }
        return nodeDOList;
    }

    @Override
    public void put(Triple<String, String, String> key, List<RegistryNodeDO> R) {

    }
}