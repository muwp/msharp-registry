package com.ruijing.registry.admin.rpc;

import com.ruijing.fundamental.remoting.msharp.annotation.MSharpService;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.enums.RegistryNodeStatusEnum;
import com.ruijing.registry.admin.service.RegistryService;
import com.ruijing.registry.admin.util.JsonUtils;
import com.ruijing.registry.admin.util.MetaUtil;
import com.ruijing.registry.client.api.RegistryServiceApi;
import com.ruijing.registry.client.dto.RegistryNodeDTO;
import com.ruijing.registry.client.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.client.dto.ServiceNodeMetaDTO;
import com.ruijing.registry.client.request.Request;
import com.ruijing.registry.client.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@MSharpService(registry = "direct")
public class RegistryServiceApiImpl implements RegistryServiceApi {

    @Autowired
    private RegistryService registryService;

    @Override
    public Response<List<String>> discovery(RegistryNodeQueryDTO query) {
        Response<List<String>> response = registryService.discovery(query);
        return response;
    }

    @Override
    public Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request) {
        Response<Map<String, List<String>>> result = registryService.discovery(request);
        return result;
    }

    @Override
    public Response<Boolean> renew(Request<RegistryNodeDTO> request) {
        List<RegistryNodeDTO> registryNodeList = request.getList();
        final List<RegistryNodeDO> registryNodeDOList = new ArrayList<>(registryNodeList.size());
        for (int i = 0, size = registryNodeList.size(); i < size; i++) {
            RegistryNodeDTO node = registryNodeList.get(i);
            ServiceNodeMetaDTO meta = JsonUtils.fromJson(node.getMeta(), ServiceNodeMetaDTO.class);
            meta.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
            RegistryNodeDO registryNodeDO = new RegistryNodeDO();
            registryNodeDO.setServiceName(node.getServiceName());
            registryNodeDO.setStatus(RegistryNodeStatusEnum.NORMAL.getCode());
            registryNodeDO.setValue(MetaUtil.convert(meta));
            registryNodeDO.setAppkey(node.getAppkey());
            registryNodeDO.setEnv(node.getEnv());
            registryNodeDO.setMetric(Optional.ofNullable(node.getMetric()).orElse(StringUtils.EMPTY));
            registryNodeDO.setMeta(Optional.ofNullable(node.getMeta()).orElse(StringUtils.EMPTY));
            registryNodeDO.setVersion(Optional.ofNullable(node.getVersion()).orElse(StringUtils.EMPTY));
            registryNodeDOList.add(registryNodeDO);
        }
        return registryService.registry(registryNodeDOList);
    }
}