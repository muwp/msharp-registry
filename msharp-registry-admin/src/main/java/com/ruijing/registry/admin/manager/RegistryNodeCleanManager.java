package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.threadpool.NamedThreadFactory;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.data.query.RegistryNodeQuery;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RegistryNodeCleanManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryNodeCleanManager implements InitializingBean {

    private static final int DEFAULT_BATCH_UPDATE_SIZE = 50;

    private static final int DEFAULT_TIME_OUT = 60 * 1000;

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService cleanExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-message-queue-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cleanExecutor.scheduleWithFixedDelay(this::cleanOverdueNode, 30, 10, TimeUnit.SECONDS);
    }

    private void cleanOverdueNode() {
        Transaction transaction = Cat.newTransaction("RegistryNodeCleanManager", "cleanOverdueNode");
        try {
            try {
                final Date nowDate = this.registryNodeMapper.getSystemDateTime();
                long time = nowDate.getTime();
                int index = 1;
                while (true) {
                    long fromIndex = (index - 1) * DEFAULT_BATCH_UPDATE_SIZE;
                    final RegistryNodeQuery query = new RegistryNodeQuery();
                    query.setOffset(fromIndex);
                    query.setPageSize(DEFAULT_BATCH_UPDATE_SIZE);
                    final List<RegistryNodeDO> registryNodeDOList = registryNodeMapper.queryForList(query);
                    if (CollectionUtils.isEmpty(registryNodeDOList)) {
                        break;
                    }

                    for (int i = 0, size = registryNodeDOList.size(); i < size; i++) {
                        final RegistryNodeDO registryNode = registryNodeDOList.get(i);
                        if (registryNode.getStatus() == null || registryNode.getStatus() == RegistryNodeStatusEnum.DELETED.getCode()) {
                            continue;
                        }
                        final long interval = time - registryNode.getUpdateTime().getTime();
                        if (interval > DEFAULT_TIME_OUT) {
                            registryNodeMapper.remove(registryNode.getId());
                        }
                    }

                    if (registryNodeDOList.size() < DEFAULT_BATCH_UPDATE_SIZE) {
                        break;
                    }
                }
            } catch (Exception ex) {
                Cat.logError("RegistryNodeCleanManager", "cleanOverdueNode", null, ex);
            }

            // registryNodeMapper.cleanData(DEFAULT_TIME_OUT);
            transaction.setSuccessStatus();
        } catch (Exception ex) {
            transaction.setStatus(ex);
        } finally {
            transaction.complete();
        }
    }
}