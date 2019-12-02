package com.ruijing.registry.admin.manager;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.data.query.RegistryQuery;
import com.ruijing.registry.admin.enums.ClientInvokerVersionEnum;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import com.ruijing.registry.admin.request.Request;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.client.model.server.RegistryNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    public Response<String> registry(final String data) {
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
        registryNodeDO.setAppkey(node.getAppkey());
        registryNodeDO.setEnv(node.getEnv());
        registryNodeDO.setServiceName(node.getServiceName());
        registryNodeDO.setValue(node.getValue());
        registryNodeDO.setMeta(StringUtils.EMPTY);
        registryNodeDO.setMetric(StringUtils.EMPTY);
        registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
        return registryService.registry(registryNodeDO);
    }

    public Response<String> remove(final Request<RegistryNode> request) {
        final List<RegistryNode> nodeList = request.getList();
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(nodeList.size());
        for (int i = 0, size = nodeList.size(); i < size; i++) {
            final RegistryNode node = nodeList.get(i);
            final RegistryNodeDO registryNode = new RegistryNodeDO();
            registryNode.setAppkey(node.getAppkey());
            registryNode.setServiceName(node.getServiceName());
            registryNode.setEnv(node.getEnv());
            registryNode.setValue(node.getValue());
            registryNodeDOList.add(registryNode);
        }
        return registryService.remove(registryNodeDOList);
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
        registryNode.setAppkey(registryData.getAppkey());
        registryNode.setEnv(registryData.getEnv());
        registryNode.setServiceName(registryData.getServiceName());
        registryNode.setValue(registryData.getValue());
        return registryService.remove(registryNode);
    }

    public Response<List<String>> discovery(RegistryQuery query) {
        final Response<List<String>> response = registryService.discovery(query, ClientInvokerVersionEnum.VERSION_0.getName());
        return response;
    }
}
