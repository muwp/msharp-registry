package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.annotation.RegistryClient;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.data.query.RegistryQuery;
import com.ruijing.registry.admin.enums.ClientInvokerVersionEnum;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import com.ruijing.registry.admin.request.Request;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.admin.util.Request2Util;
import com.ruijing.registry.client.model.v2.RegistryNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Api2Controller
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
@RequestMapping("/api/v2")
public class Api2Controller {

    @Resource
    private RegistryService registryService;

    /**
     * 服务注册 & 续约 API
     * <p>
     * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
     * <p>
     * ------
     * 地址格式：{服务注册中心跟地址}/registry
     * <p>
     * 请求参数说明：
     * 1、appkey：业务标识
     * 2、env：环境标识
     * 3、serviceName:服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "appkey" : "xx",
     * "env" : "xx",
     * "serviceName" : "service01",
     * "value" : "address01"
     * }
     */
    @RequestMapping("/renew")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> renew(@RequestBody(required = false) String data) {
        final Request<RegistryNode> request = Request2Util.getServerRequest(data);
        if (null == request) {
            return Response.FAIL;
        }
        List<RegistryNode> registryNodeList = request.getList();
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNode node = registryNodeList.get(i);
            RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setAppkey(node.getAppkey());
            registryNodeDO.setServiceName(node.getServiceName());
            registryNodeDO.setValue(node.getValue());
            registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
            registryNodeDO.setEnv(node.getEnv());
            registryNodeDO.setMeta(Optional.ofNullable(node.getMeta()).orElse(StringUtils.EMPTY));
            registryNodeDO.setVersion(Optional.ofNullable(node.getVersion()).orElse(StringUtils.EMPTY));
            registryNodeDO.setMetric(Optional.ofNullable(node.getMetric()).orElse(StringUtils.EMPTY));
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(registryNodeDOList);
    }

    /**
     * 服务发现 API
     * <p>
     * 说明：查询在线服务地址列表；
     * <p>
     * ------
     * 地址格式：{服务注册中心跟地址}/discovery
     * <p>
     * 请求参数说明：
     * 1、appkey：业务标识
     * 2、env：环境标识
     * 3、serviceName：服务注册Key列表
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "appkey" : "pearl-service",
     * "env" : "test",
     * "serviceName":"serviceName"
     * }
     */
    @RequestMapping("/find")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public String find(@RequestBody(required = false) String data) {
        final Request<RegistryQuery> request = Request2Util.getClientRequest(data);
        if (null == request) {
            return JsonUtils.toJson(Response.FAIL_);
        }

        Object result;
        if (request.getMode() == 0 && request.getList().size() == 1) {
            result = registryService.discovery(request.getList().get(0), ClientInvokerVersionEnum.VERSION_0.getName());
        } else {
            result = registryService.discovery(request, ClientInvokerVersionEnum.VERSION_0.getName());
        }
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/discovery")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public String discovery(@RequestBody(required = false) String data) {
        final Request<RegistryQuery> request = Request2Util.getClientRequest(data);
        if (null == request) {
            return JsonUtils.toJson(Response.FAIL_);
        }

        Object result;
        if (request.getMode() == 0 && request.getList().size() == 1) {
            result = registryService.discovery(request.getList().get(0), ClientInvokerVersionEnum.VERSION_2.getName());
        } else {
            result = registryService.discovery(request, ClientInvokerVersionEnum.VERSION_2.getName());
        }
        return JsonUtils.toJson(result);
    }


    @RequestMapping("/offline")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> remove(@RequestBody(required = false) String data) {
        final Request<RegistryNode> request = Request2Util.getServerRequest(data);
        if (null == request) {
            return Response.FAIL;
        }
        List<RegistryNode> registryNodeList = request.getList();
        List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            RegistryNode node = registryNodeList.get(i);
            RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setServiceName(node.getServiceName());
            registryNode.setAppkey(node.getAppkey());
            registryNode.setEnv(node.getEnv());
            registryNode.setValue(node.getValue());
            registryNodeDOList.add(registryNode);
        }
        return registryService.remove(registryNodeDOList);
    }
}