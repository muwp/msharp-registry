package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.model.ReturnT;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.client.model.MSharpRegistryData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
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
public class SimpleApiController {

    @Value("${msharp.registry.accessToken}")
    private String accessToken;

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
     * 1、accessToken：请求令牌；
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、registryDataList：服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "accessToken" : "xx",
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
    public ReturnT<String> registry(@RequestBody(required = false) String data) {
        // parse data
        MSharpRegistryData registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, MSharpRegistryData.class);
        } catch (Exception e) {
            Cat.logError("method:registry,data:" + data, e);
        }
        if (null == registryData) {
            return ReturnT.FAIL;
        }
        final RegistryNodeDO xxlRegistryData = new RegistryNodeDO();
        xxlRegistryData.setBiz(registryData.getBiz());
        xxlRegistryData.setEnv(registryData.getEnv());
        xxlRegistryData.setKey(registryData.getKey());
        xxlRegistryData.setValue(registryData.getValue());
        return registryService.registry(registryData.getAccessToken(), Arrays.asList(xxlRegistryData));
    }

    @RequestMapping("/batch/registry")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> batchRegistry(@RequestBody(required = false) String data) {
        // parse data
        List<MSharpRegistryData> registryDataList = null;
        try {
            registryDataList = JsonUtils.parseList(data, MSharpRegistryData.class);
        } catch (Exception e) {
            Cat.logError("method:batchRegistry,data:" + data, e);
        }

        if (CollectionUtils.isEmpty(registryDataList)) {
            return ReturnT.FAIL;
        }
        String accessToken = null;
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryDataList.size());
        for (final MSharpRegistryData registryData : registryDataList) {
            if (StringUtils.isEmpty(accessToken)) {
                accessToken = registryData.getAccessToken();
            }
            final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setKey(registryData.getKey());
            registryNodeDO.setValue(registryData.getValue());
            registryNodeDO.setBiz(registryData.getBiz());
            registryNodeDO.setEnv(registryData.getEnv());
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(accessToken, registryNodeDOList);
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
     * 1、accessToken：请求令牌；
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、registryDataList：服务注册信息
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "accessToken" : "xx",
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
    public ReturnT<String> remove(@RequestBody(required = false) String data) {
        // parse data
        MSharpRegistryData registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, MSharpRegistryData.class);
        } catch (Exception e) {
            Cat.logError("method:remove,data:" + data, e);
        }
        if (null == registryData) {
            return ReturnT.FAIL;
        }
        RegistryNodeDO registryNodeDO = new RegistryNodeDO();
        registryNodeDO.setBiz(registryData.getBiz());
        registryNodeDO.setEnv(registryData.getEnv());
        registryNodeDO.setKey(registryData.getKey());
        registryNodeDO.setValue(registryData.getValue());
        return registryService.remove(registryData.getAccessToken(), registryNodeDO);
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
     * 1、accessToken：请求令牌；
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、keys：服务注册Key列表
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "accessToken" : "xx",
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
    public ReturnT<List<String>> discovery(@RequestBody(required = false) String data) {
        // parse data
        MSharpRegistryData registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, MSharpRegistryData.class);
        } catch (Exception e) {
            Cat.logError("method:discovery,data:" + data, e);
        }
        if (null == registryData) {
            return null;
        }
        final ReturnT<List<String>> returnT = registryService.discovery(registryData.getAccessToken(), registryData.getBiz(), registryData.getEnv(), registryData.getKey());
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
     * 1、accessToken：请求令牌；
     * 2、biz：业务标识
     * 2、env：环境标识
     * 3、keys：服务注册Key列表
     * <p>
     * 请求数据格式如下，放置在 RequestBody 中，JSON格式：
     * <p>
     * {
     * "accessToken" : "xx",
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
    public DeferredResult monitor(@RequestBody(required = false) String data) {
        // parse data
        MSharpRegistryData registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, MSharpRegistryData.class);
        } catch (Exception e) {
            Cat.logError("method:monitor,data:" + data, e);
        }
        return registryService.monitor(registryData.getAccessToken(), registryData.getBiz(), registryData.getEnv(), Arrays.asList(registryData.getKey()));
    }
}