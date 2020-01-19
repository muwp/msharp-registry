package com.ruijing.registry.admin.service.impl;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.builder.JsonObjectBuilder;
import com.ruijing.fundamental.common.collections.New;
import com.ruijing.registry.admin.cache.RegistryCache;
import com.ruijing.registry.admin.cache.RegistryNodeCache;
import com.ruijing.registry.admin.constants.ResponseConst;
import com.ruijing.registry.admin.data.model.ClientNodeDO;
import com.ruijing.registry.admin.enums.RegistryStatusEnum;
import com.ruijing.registry.admin.manager.DiscoveryManager;
import com.ruijing.registry.admin.manager.RegistryManager;
import com.ruijing.registry.api.dto.NodeMetaDTO;
import com.ruijing.registry.admin.service.RegistryManagerService;
import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.util.JsonUtil;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.api.request.Request;
import com.ruijing.registry.api.response.Response;
import com.ruijing.registry.common.http.Separator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * RegistryServiceImpl
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryManagerServiceImpl implements RegistryManagerService {

    private static final Response<List<String>> EMPTY_RETURN_LIST = new Response<>(Collections.emptyList());

    @Resource
    private RegistryCache registryCache;

    @Resource
    private RegistryNodeCache registryNodeCache;

    @Resource
    private DiscoveryManager discoveryManager;

    @Autowired
    private RegistryManager registryManager;

    @Override
    public Response<Boolean> registry(RegistryNodeDO registryNode) {
        this.registryManager.addRegistryNode(registryNode);
        return ResponseConst.SUCCESS;
    }

    @Override
    public Response<Boolean> registry(List<RegistryNodeDO> registryNodeList) {
        this.registryManager.addRegistryNodeList(registryNodeList);
        return ResponseConst.SUCCESS;
    }

    @Override
    public Response<Boolean> remove(List<RegistryNodeDO> registryNodeList) {
        if (CollectionUtils.isEmpty(registryNodeList)) {
            return new Response<>(Response.FAIL_CODE, "Registry DataList Invalid");
        }
        registryManager.removeRegistryNodeList(registryNodeList);
        return ResponseConst.SUCCESS;
    }

    @Override
    public Response<Boolean> remove(final RegistryNodeDO registryNode) {
        registryManager.removeRegistryNode(registryNode);
        return ResponseConst.SUCCESS;
    }

    @Override
    public Response<Map<String, List<String>>> discovery(final Request<RegistryNodeQueryDTO> request) {
        final List<RegistryNodeQueryDTO> queries = request.getList();
        final Map<String, List<String>> result = New.mapWithCapacity(queries.size());
        for (int i = 0, size = queries.size(); i < size; i++) {
            RegistryNodeQueryDTO query = queries.get(i);
            Response<List<String>> returnT = discovery(query);
            result.put(query.getAppkey() + Separator.LOW_MINUS + query.getEnv() + Separator.LOW_MINUS + query.getServiceName(), returnT.getData());
        }
        return new Response<>(result);
    }

    @Override
    public Response<List<String>> discovery(RegistryNodeQueryDTO query) {
        final String clientAppkey = query.getClientAppkey();
        final String appkey = query.getAppkey();
        final String env = query.getEnv();
        final String serviceName = query.getServiceName();

        if (StringUtils.isNotBlank(clientAppkey)) {
            final ClientNodeDO clientNode = new ClientNodeDO();
            clientNode.setClientAppkey(clientAppkey);
            clientNode.setEnv(env);
            clientNode.setAppkey(query.getAppkey());
            clientNode.setServiceName(serviceName);
            clientNode.setUpdateTime(new Date());
            this.discoveryManager.addClientNode(clientNode);
        }

        final RegistryDO registryDO = registryCache.get(appkey, env, serviceName);

        if (null == registryDO) {
            Cat.logEvent("discovery[no_registry]", JsonObjectBuilder.custom().put("clientAppkey", clientAppkey).put("appkey", appkey).put("env", env).put("serviceName", serviceName).build().toString(), Transaction.ERROR, "");
            return EMPTY_RETURN_LIST;
        }

        if (registryDO.getStatus() == RegistryStatusEnum.LOCKED.getCode() || registryDO.getStatus() == RegistryStatusEnum.FORBID.getCode()) {
            return new Response(JsonUtil.parseList(registryDO.getData(), String.class));
        }

        final List<RegistryNodeDO> registryNodeList = this.registryNodeCache.get(registryDO.getId());

        if (CollectionUtils.isEmpty(registryNodeList)) {
            Cat.logEvent("discovery[no_registry_node]", JsonObjectBuilder.custom().put("clientAppkey", clientAppkey).put("appkey", appkey).put("env", env).put("serviceName", serviceName).build().toString(), Transaction.ERROR, "");
            return EMPTY_RETURN_LIST;
        }

        final List<String> list = new ArrayList<>(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            final RegistryNodeDO nodeDO = registryNodeList.get(i);
            final NodeMetaDTO metaDTO = JsonUtil.fromJson(nodeDO.getMeta(), NodeMetaDTO.class);
            if (StringUtils.isNotBlank(query.getTransportType()) && StringUtils.isNotBlank(metaDTO.getTransportType()) && !query.getTransportType().equals(metaDTO.getTransportType())) {
                continue;
            }
            list.add(nodeDO.getMeta());
        }
        return new Response<>(list);
    }
}