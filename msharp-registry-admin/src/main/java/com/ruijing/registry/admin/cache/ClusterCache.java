package com.ruijing.registry.admin.cache;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.fundamental.common.env.Environment;
import com.ruijing.fundamental.mhttp.common.HttpClientHelper;
import com.ruijing.fundamental.mhttp.common.Separator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ClusterCache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class ClusterCache implements InitializingBean {

    private static final String cluster_node_list = "cluster_node_list";

    private List<String> clusterNodeList;

    private int syncDataPort;

    private static Cache<String, List<String>> clusterNodeListCache = CacheBuilder
            .newBuilder()
            .maximumSize(10)
            .expireAfterWrite(120, TimeUnit.SECONDS)
            .build();

    /**
     * 轮询服务
     */
    private final ScheduledExecutorService clusterExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("client-node-sync-update-thread", true));

    @Override
    public void afterPropertiesSet() throws Exception {
        String syncNodeList = Environment.getProperty("registry.node.sync.list");
        if (StringUtils.isBlank(syncNodeList)) {
            return;
        }
        this.syncDataPort = Environment.getInt("registry.node.sync.port", 42000);
        clusterNodeList = New.list(syncNodeList.split(","));
        clusterExecutor.scheduleWithFixedDelay(this::scheduledUpdateCluster, 5, 60, TimeUnit.SECONDS);
    }

    public void scheduledUpdateCluster() {
        for (int i = 0, size = clusterNodeList.size(); i < size; i++) {
            String url = Separator.HTTP__SCHEME + clusterNodeList.get(i) + ":8081/msharp-admin/cluster/broadcast?url=" + clusterNodeList.get(i) + ":" + syncDataPort;
            try {
                HttpClientHelper.INSTANCE.post(url, 10000);
            } catch (Exception ex) {
                Cat.logError("ClusterCache", "scheduledUpdateCluster", null, ex);
            }
        }
    }

    public List<String> getClusterNodeList() {
        List<String> nodeList = clusterNodeListCache.getIfPresent(cluster_node_list);
        return nodeList;
    }

    public void addNodeUrl(String url) {
        List<String> nodeList = clusterNodeListCache.getIfPresent(cluster_node_list);
        if (null == nodeList) {
            nodeList = new ArrayList<>();
            clusterNodeListCache.put("cluster_node_list", nodeList);
        }

        if (!nodeList.contains(url)) {
            nodeList.add(url);
        }
    }

    public int getSyncDataPort() {
        return syncDataPort;
    }
}
