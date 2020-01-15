package com.ruijing.registry.api.service;

import com.ruijing.registry.api.dto.RegistryNodeDTO;
import com.ruijing.registry.api.dto.RegistryNodeQueryDTO;
import com.ruijing.registry.api.request.Request;
import com.ruijing.registry.api.response.Response;

import java.util.List;
import java.util.Map;

/**
 * RegistryService
 *
 * @author mwup
 * @version 1.0
 * @created 2020/01/14 17:03
 **/
public interface RegistryService {

    Response<List<String>> discovery(RegistryNodeQueryDTO query);

    Response<Map<String, List<String>>> discovery(Request<RegistryNodeQueryDTO> request);

    Response<Boolean> renew(Request<RegistryNodeDTO> request);
}