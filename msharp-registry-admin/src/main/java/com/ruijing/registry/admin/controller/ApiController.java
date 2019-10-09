package com.ruijing.registry.admin.controller;

import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.model.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.client.model.XxlRegistryDataParamVO;
import com.ruijing.registry.client.model.XxlRegistryParamVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApiController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Controller
@RequestMapping("/api")
@Deprecated
public class ApiController {

    @Resource
    private RegistryService xxlRegistryService;

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
     * "registryDataList" : [{
     * "key" : "service01",
     * "value" : "address01"
     * }]
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/registry")
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<String> registry(@RequestBody(required = false) String data) {

        // parse data
        XxlRegistryParamVO registryParamVO = null;
        try {
            registryParamVO = JsonUtils.fromJson(data, XxlRegistryParamVO.class);
        } catch (Exception e) {
        }

        // parse param
        String accessToken = null;
        String biz = null;
        String env = null;
        List<RegistryNodeDO> registryDataList = null;

        if (registryParamVO != null) {
            accessToken = registryParamVO.getAccessToken();
            biz = registryParamVO.getBiz();
            env = registryParamVO.getEnv();
            if (registryParamVO.getRegistryDataList() != null) {
                registryDataList = new ArrayList<>();
                for (XxlRegistryDataParamVO dataParamVO : registryParamVO.getRegistryDataList()) {
                    RegistryNodeDO dateItem = new RegistryNodeDO();
                    dateItem.setKey(dataParamVO.getKey());
                    dateItem.setValue(dataParamVO.getValue());
                    registryDataList.add(dateItem);
                }
            }
        }

        return xxlRegistryService.registry(accessToken, registryDataList);
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
     * "registryDataList" : [{
     * "key" : "service01",
     * "value" : "address01"
     * }]
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<String> remove(@RequestBody(required = false) String data) {

        // parse data
        XxlRegistryParamVO registryParamVO = null;
        try {
            registryParamVO = JsonUtils.fromJson(data, XxlRegistryParamVO.class);
        } catch (Exception e) {
        }

        // parse param
        String accessToken = null;
        String biz = null;
        String env = null;
        List<RegistryNodeDO> registryDataList = null;

        if (registryParamVO != null) {
            accessToken = registryParamVO.getAccessToken();
            biz = registryParamVO.getBiz();
            env = registryParamVO.getEnv();
            if (registryParamVO.getRegistryDataList() != null) {
                registryDataList = new ArrayList<>();
                for (XxlRegistryDataParamVO dataParamVO : registryParamVO.getRegistryDataList()) {
                    RegistryNodeDO dateItem = new RegistryNodeDO();
                    dateItem.setKey(dataParamVO.getKey());
                    dateItem.setValue(dataParamVO.getValue());
                    registryDataList.add(dateItem);
                }
            }
        }

        return xxlRegistryService.remove(accessToken, registryDataList);
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
     * "keys" : [
     * "service01",
     * "service02"
     * ]
     * }
     *
     * @param data
     * @return
     */
    @RequestMapping("/discovery")
    @ResponseBody
    @PermissionLimit(limit = false)
    public Response<Map<String, List<String>>> discovery(@RequestBody(required = false) String data) {

        // parse data
        XxlRegistryParamVO registryParamVO = null;
        try {
            registryParamVO = JsonUtils.fromJson(data, XxlRegistryParamVO.class);
        } catch (Exception e) {
        }

        // parse param
        String accessToken = null;
        String biz = null;
        String env = null;
        List<String> keys = null;

        if (registryParamVO != null) {
            accessToken = registryParamVO.getAccessToken();
            biz = registryParamVO.getBiz();
            env = registryParamVO.getEnv();
            keys = registryParamVO.getKeys();
        }

        return xxlRegistryService.discovery(accessToken, biz, env, keys);
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
     * "keys" : [
     * "service01",
     * "service02"
     * ]
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
        XxlRegistryParamVO registryParamVO = null;
        try {
            registryParamVO = JsonUtils.fromJson(data, XxlRegistryParamVO.class);
        } catch (Exception e) {

        }

        // parse param
        String accessToken = null;
        String biz = null;
        String env = null;
        List<String> keys = null;

        if (registryParamVO != null) {
            accessToken = registryParamVO.getAccessToken();
            biz = registryParamVO.getBiz();
            env = registryParamVO.getEnv();
            keys = registryParamVO.getKeys();
        }

        return xxlRegistryService.monitor(accessToken, biz, env, keys);
    }
}
