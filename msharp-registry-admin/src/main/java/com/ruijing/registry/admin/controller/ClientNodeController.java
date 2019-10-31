package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.api.remote.RemoteResponse;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.cache.ClientNodeCache;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * ClientNodeController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@RestController
@RequestMapping(value = "/invoker")
public class ClientNodeController {

    @Autowired
    private ClientNodeCache clientNodeCache;

    @PermissionLimit(limit = false)
    @RequestMapping(value = "/consumers")
    @ResponseBody
    public RemoteResponse<List<Map<String, String>>> token(@RequestParam(value = "appkey") String appkey, @RequestParam(value = "serviceName") String serviceName, @RequestParam(value = "env") String env) {
        final List<ClientNodeDO> clientNodeList = this.clientNodeCache.syncGet(appkey, env, serviceName);
        if (CollectionUtils.isEmpty(clientNodeList)) {
            return RemoteResponse.custom().setSuccess().build();
        }
        final List<Map<String, String>> list = new ArrayList<>(clientNodeList.size());
        for (int i = 0, size = clientNodeList.size(); i < size; i++) {
            final ClientNodeDO clientNode = clientNodeList.get(i);
            final Map<String, String> map = new HashMap<>();
            map.put("clientAppkey", clientNode.getClientAppkey());
            map.put("env", clientNode.getEnv());
            map.put("serviceName", clientNode.getServiceName());
            map.put("remoteAppkey", clientNode.getAppkey());
            list.add(map);
        }
        return RemoteResponse.custom().setData(list).setSuccess().build();
    }
}