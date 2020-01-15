package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.api.request.Request;
import com.ruijing.registry.api.response.Response;

import java.util.List;
import java.util.Map;

/**
 * RegistryManagerService
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface RegistryManagerService {

    Response<Boolean> registry(RegistryNodeDO registryNode);

    Response<Boolean> registry(List<RegistryNodeDO> registryNodeList);

    Response<Boolean> remove(List<RegistryNodeDO> registryNodeList);

    Response<Boolean> remove(RegistryNodeDO registryNode);

    Response<List<String>> discovery(RegistryNodeQueryDTO query);

    Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request);
}
