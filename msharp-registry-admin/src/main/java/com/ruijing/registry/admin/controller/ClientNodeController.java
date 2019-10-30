package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.api.remote.RemoteResponse;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.cache.ClientNodeCache;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public RemoteResponse<List<ClientNodeDO>> token(@RequestParam(value = "appkey") String appkey, @RequestParam(value = "serviceName") String serviceName, @RequestParam(value = "env") String env) {
        final List<ClientNodeDO> clientNodeList = this.clientNodeCache.syncGet(appkey, env, serviceName);
        return RemoteResponse.custom().setData(clientNodeList).setSuccess().build();
    }
}