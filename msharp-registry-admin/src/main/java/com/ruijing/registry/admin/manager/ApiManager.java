package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.builder.JsonObjectBuilder;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import com.ruijing.registry.admin.request.Request;
import com.ruijing.registry.client.model.client.RegistryNodeQuery;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.client.model.server.RegistryNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ApiManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class ApiManager {

    @Resource
    private RegistryService registryService;

    public Response<String> registry(String data) {
        // parse data
        RegistryNode node = null;
        try {
            node = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("apiController.registry,data:" + data, e);
        }

        if (null == node) {
            return Response.FAIL;
        }
        final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
        registryNodeDO.setBiz(node.getBiz());
        registryNodeDO.setEnv(node.getEnv());
        registryNodeDO.setKey(node.getKey());
        registryNodeDO.setValue(node.getValue());
        registryNodeDO.setMeta(StringUtils.EMPTY);
        registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
        return registryService.registry(registryNodeDO);
    }

    public Response<String> batchRegistry(final String data) {
        // parse data
        List<RegistryNode> registryNodeList = null;
        try {
            registryNodeList = JsonUtils.parseList(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("apiController.batchRegistry,data:" + data, e);
        }
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return Response.FAIL;
        }
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryNodeList.size());
        for (final RegistryNode node : registryNodeList) {
            final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setKey(node.getKey());
            registryNodeDO.setValue(node.getValue());
            registryNodeDO.setBiz(node.getBiz());
            registryNodeDO.setEnv(node.getEnv());
            registryNodeDO.setMeta(StringUtils.EMPTY);
            registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(registryNodeDOList);
    }

    public Response<String> renew(final Request<RegistryNode> request) {
        // parse data
        final List<RegistryNode> registryNodeList = request.getList();
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryNodeList.size());
        for (final RegistryNode node : registryNodeList) {
            final RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setKey(node.getKey());
            registryNodeDO.setBiz(node.getBiz());
            registryNodeDO.setValue(node.getValue());
            registryNodeDO.setEnv(node.getEnv());
            registryNodeDO.setMeta(node.getMeta());
            registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(registryNodeDOList);
    }

    public Response<String> remove(String data) {
        // parse data
        RegistryNode registryData = null;
        try {
            registryData = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("apiController.remove,data:" + data, e);
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

    public Response<List<String>> discovery(String data) {
        // parse data
        RegistryNodeQuery query = null;
        try {
            query = JsonUtils.fromJson(data, RegistryNodeQuery.class);
        } catch (Exception e) {
            Cat.logError("apiController.discovery,data:" + data, e);
        }
        if (null == query) {
            return null;
        }
        final Response<List<String>> returnT = registryService.discovery(query);
        return returnT;
    }

    public Response<Map<String, List<String>>> discovery(Request<RegistryNodeQuery> request) {
        final Response<Map<String, List<String>>> returnT = registryService.discovery(request);
        return returnT;
    }

    public DeferredResult monitor(String data) {
        // parse data
        RegistryNode registryNode = null;
        try {
            registryNode = JsonUtils.fromJson(data, RegistryNode.class);
        } catch (Exception e) {
            Cat.logError("apiController.monitor,data:" + data, e);
        }

        if (null == registryNode) {
            DeferredResult result = new DeferredResult();
            result.setResult(new Response<>(Response.FAIL_CODE, "Monitor key update."));
            return result;
        }
        Cat.logEvent("monitor", JsonObjectBuilder.custom().put("clientAppkey", registryNode.getClientAppkey()).put("biz", registryNode.getBiz()).put("env", registryNode.getEnv()).put("key", registryNode.getKey()).build().toString(), Transaction.ERROR, "");
        return registryService.monitor(registryNode.getBiz(), registryNode.getEnv(), Arrays.asList(registryNode.getKey()));
    }
}
