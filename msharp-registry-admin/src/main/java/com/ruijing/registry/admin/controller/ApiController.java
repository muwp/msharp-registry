package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.annotation.RegistryClient;
import com.ruijing.registry.admin.cache.TokenCache;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.client.model.RegistryNode;
import com.ruijing.registry.admin.model.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * SimpleApiController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
@RequestMapping("/simple/api")
public class ApiController {

    public static final Response<List<String>> FORBIDDEN = new Response<>(403, null);


    @Autowired
    private TokenCache tokenCache;

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
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、registryDataList：服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "biz" : "xx",
     * "env" : "xx",
     * "key" : "service01",
     * "value" : "address01"
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/registry")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> registry(@RequestBody(required = false) String data) {

        // parse data
        RegistryNode registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("method:registry,data:" + data, e);
        }

        if (null == registryData) {
            return Response.FAIL;
        }
        final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
        registryNodeDO.setBiz(registryData.getBiz());
        registryNodeDO.setEnv(registryData.getEnv());
        registryNodeDO.setKey(registryData.getKey());
        registryNodeDO.setValue(registryData.getValue());
        return registryService.registry(Arrays.asList(registryNodeDO));
    }

    @RequestMapping("/batch/registry")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> batchRegistry(@RequestBody(required = false) String data) {
        // parse data
        List<RegistryNode> registryDataList = null;
        try {
            registryDataList = JsonUtils.parseList(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("method:batchRegistry,data:" + data, e);
        }

        if (CollectionUtils.isEmpty(registryDataList)) {
            return Response.FAIL;
        }
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryDataList.size());
        for (final RegistryNode registryData : registryDataList) {
            final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setKey(registryData.getKey());
            registryNodeDO.setValue(registryData.getValue());
            registryNodeDO.setBiz(registryData.getBiz());
            registryNodeDO.setEnv(registryData.getEnv());
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(registryNodeDOList);
    }

    /**
     * 服务摘除 API
     * <p>
     * 说明：新服务摘除下线1s内广播通知接入方；
     * <p>
     * ------
     * 地址格式：{服务注册中心跟地址}/remove
     * <p>
     * 请求参数说明：
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、registryDataList：服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "biz" : "xx",
     * "env" : "xx",
     * "key" : "service01",
     * "value" : "address01"
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<String> remove(@RequestBody(required = false) String data) {
        // parse data
        RegistryNode registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("method:remove,data:" + data, e);
        }
        if (null == registryData) {
            return Response.FAIL;
        }
        RegistryNodeDO registryNode = new RegistryNodeDO();
        registryNode.setBiz(registryData.getBiz());
        registryNode.setEnv(registryData.getEnv());
        registryNode.setKey(registryData.getKey());
        registryNode.setValue(registryData.getValue());
        return registryService.remove(registryNode);
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
     * "biz" : "xx",
     * "env" : "xx",
     * "service01",
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/discovery")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public Response<List<String>> discovery(@RequestBody(required = false) String data) {
        // parse data
        RegistryNode registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("method:discovery,data:" + data, e);
        }
        if (null == registryData) {
            return null;
        }
        final Response<List<String>> returnT = registryService.discovery(registryData.getBiz(), registryData.getEnv(), registryData.getKey());
        return returnT;
    }

    /**
     * 服务监控 API
     * <p>
     * 说明：long-polling 接口，主动阻塞一段时间（三倍于注册中心心跳时间）；直至阻塞超时或服务注册信息变动时响应；
     * <p>
     * ------
     * 地址格式：{服务注册中心跟地址}/monitor
     * <p>
     * 请求参数说明：
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、keys：服务注册Key列表
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "biz" : "xx",
     * "env" : "xx",
     * "service02"
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/monitor")
    @ResponseBody
    @PermissionLimit(limit = false)
    @RegistryClient
    public DeferredResult monitor(@RequestBody(required = false) String data) {
        // parse data
        RegistryNode registryNode = null;
        try {
            registryNode = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("method:monitor,data:" + data, e);
        }

        if (null == registryNode) {
            DeferredResult result = new DeferredResult();
            result.setResult(new Response<>(Response.FAIL_CODE, "Monitor key update."));
            return result;
        }

        return registryService.monitor(registryNode.getBiz(), registryNode.getEnv(), Arrays.asList(registryNode.getKey()));
    }

    private Response<String> valid(HttpServletRequest request) {
        final String accessToken = request.getHeader("access_token");
        final String clientAppkey = request.getHeader("client_appkey");
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }

        final String serverToken = tokenCache.get(clientAppkey);
        if (StringUtils.isBlank(serverToken)) {
            return null;
        }

        if (serverToken.trim().equalsIgnoreCase(accessToken)) {
            return null;
        }

        return Response.FORBIDDEN;
    }
}