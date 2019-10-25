package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.annotation.RegistryClient;
import com.ruijing.registry.admin.manager.ApiManager;
import com.ruijing.registry.admin.request.Request;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.admin.util.RequestUtil;
import com.ruijing.registry.client.model.client.RegistryNodeQuery;
import com.ruijing.registry.client.model.server.RegistryNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * ApiController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
@RequestMapping("/api")
public class Api2Controller {

    @Resource
    private ApiManager apiManager;

    /**
     * 服务注册 & 续约 API
     * <p>
     * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
     * <p>
     * ------
     * 地址格式：{服务注册中心跟地址}/registry
     * <p>
     * 请求参数说明：
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、registryNodeList:服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "biz" : "xx",
     * "env" : "xx",
     * "key" : "service01",
     * "value" : "address01"
     * }
     */
    @RequestMapping("/renew")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> renew(@RequestBody(required = false) String data) {
        final Request<RegistryNode> request = RequestUtil.getServerRequest(data);
        if (null == request) {
            return Response.FAIL;
        }
        return apiManager.renew(request);
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
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、keys：服务注册Key列表
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "biz" : "pearl-service",
     * "env" : "test",
     * "key":"serviceName"
     * }
     */
    @RequestMapping("/find")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public String find(@RequestBody(required = false) String data) {
        final Request<RegistryNodeQuery> request = RequestUtil.getClientRequest(data);
        if (null == request) {
            return JsonUtils.toJson(Response.FAIL_);
        }
        Object result;
        if (request.getMode() == 0 && request.getList().size() == 1) {
            result = apiManager.discovery(request.getList().get(0));
        } else {
            result = apiManager.discovery(request);
        }
        return JsonUtils.toJson(result);
    }
}