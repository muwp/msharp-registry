package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.threadpool.NamedThreadFactory;
import com.ruijing.registry.admin.data.mapper.RegistryNodeMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private RegistryNodeMapper registryNodeMapper;

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService cleanExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("pearl-message-queue-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cleanExecutor.scheduleWithFixedDelay(this::cleanOverdueNode, 30, 8, TimeUnit.SECONDS);
    }

    private void cleanOverdueNode() {
        try {
            // clean old registry-data in db
            registryNodeMapper.cleanData(60);
        } catch (Exception ex) {
            Cat.logError("RegistryManager", "cleanOverdueRegistryNode", null, ex);
        }
    }
}