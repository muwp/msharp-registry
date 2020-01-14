package com.ruijing.registry.client.api;

import com.ruijing.registry.client.dto.RegistryNodeDTO;
import com.ruijing.registry.client.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.client.request.Request;
import com.ruijing.registry.client.response.Response;

import java.util.List;
import java.util.Map;

/**
 * RegistryServiceApi
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface RegistryServiceApi {

    Response<List<String>> discovery(RegistryNodeQueryDTO query);

    Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request);

    Response<Boolean> renew(Request<RegistryNodeDTO> request);
}