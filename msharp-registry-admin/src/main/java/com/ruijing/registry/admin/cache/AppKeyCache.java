package com.ruijing.registry.admin.cache;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.threadpool.NamedThreadFactory;
import org.springframework.beans.factory.InitializingBean;

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
public class AppKeyCache implements InitializingBean {

    private volatile Set<String> registryAppkeySet = new ConcurrentHashSet<>();

    public boolean contains(String appkey) {
        return registryAppkeySet.contains(appkey);
    }

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService registryUpdateExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("registry-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registryUpdateExecutor.scheduleWithFixedDelay(this::updateAppkeySet, 1, 30, TimeUnit.MINUTES);
    }

    private void updateAppkeySet() {
        try {

        } catch (Exception ex) {
            Cat.logError("AppKeyCache", "updateAppkeySet", null, ex);
        }
    }
}