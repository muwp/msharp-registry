package com.ruijing.registry.admin.util;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.registry.client.dto.RegistryNodeDTO;
import com.ruijing.registry.client.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.client.request.Request;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Request2Util
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class Request2Util {

    public static Request<RegistryNodeQueryDTO> getClientRequest(final String json) {
        Request request = getRequest(json);
        if (request == null || CollectionUtils.isEmpty(request.getList())) {
            return request;
        }

        List<Map<String, String>> mapList = request.getList();
        List<RegistryNodeQueryDTO> queryList = new ArrayList<>(mapList.size());
        for (int i = 0, size = mapList.size(); i < size; i++) {
            RegistryNodeQueryDTO query = new RegistryNodeQueryDTO();
            final Map<String, String> map = mapList.get(i);
            query.setAppkey(map.get("appkey"));
            query.setClientAppkey(map.get("clientAppkey"));
            query.setEnv(map.get("env"));
            query.setServiceName(map.get("serviceName"));
            query.setGroup(map.get("group"));
            query.setTransportType(map.get("transportType"));
            query.setScope(map.get("scope"));
            queryList.add(query);
        }
        request.setList(queryList);
        return request;
    }

    private static Request<RegistryNodeQueryDTO> getRequest(final String json) {
        Request request = null;
        try {
            request = JsonUtils.fromJson(json, Request.class);
        } catch (Exception e) {
            Cat.logError("ClientRequestUtil.getRequest,data:" + json, e);
        }
        return request;
    }

    public static Request<RegistryNodeDTO> getServerRequest(final String json) {
        final Request request = getRequest(json);
        if (request == null || CollectionUtils.isEmpty(request.getList())) {
            return null;
        }
        final List<Map<String, String>> mapList = request.getList();
        final List<RegistryNodeDTO> nodeList = new ArrayList<>(mapList.size());
        for (int i = 0, size = mapList.size(); i < size; i++) {
            final Map<String, String> map = mapList.get(i);
            final RegistryNodeDTO node = new RegistryNodeDTO();
            node.setClientAppkey(map.get("clientAppkey"));
            node.setAppkey(map.get("appkey"));
            node.setServiceName(map.get("serviceName"));
            node.setEnv(map.get("env"));
            node.setMeta(Optional.ofNullable(map.get("meta")).orElse(StringUtils.EMPTY));
            node.setVersion(Optional.ofNullable(map.get("version")).orElse(StringUtils.EMPTY));
            node.setMetric(Optional.ofNullable(map.get("metric")).orElse(StringUtils.EMPTY));
            nodeList.add(node);
        }
        request.setList(nodeList);
        return request;
    }
}