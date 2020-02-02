package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.remoting.msharp.util.NetUtil;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.cache.ClusterCache;
import com.ruijing.registry.admin.constants.ResponseConst;
import com.ruijing.registry.api.response.Response;
import com.ruijing.registry.common.http.Separator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * ClusterManagerController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@RestController
@RequestMapping(value = "/cluster")
public class ClusterManagerController {

    @Autowired
    private ClusterCache clusterCache;

    @RequestMapping("/list")
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<List<String>> list() {
        final String localIp = NetUtil.getIpV4();
        List<String> nodeList = clusterCache.getClusterNodeList();
        if (StringUtils.isBlank(localIp)) {
            return new Response<>(nodeList);
        }

        if (CollectionUtils.isEmpty(nodeList)) {
            return new Response<>(Collections.singletonList(localIp + Separator.COLON + clusterCache.getSyncDataPort()));
        } else {
            return new Response<>(nodeList);
        }
    }

    @RequestMapping("/broadcast")
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<Boolean> broadcast(@RequestParam(value = "url") String url) {
        clusterCache.addNodeUrl(url);
        return ResponseConst.SUCCESS;
    }
}