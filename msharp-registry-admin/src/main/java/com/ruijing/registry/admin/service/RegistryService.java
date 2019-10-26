package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.request.Request;
import com.ruijing.registry.admin.response.Response;
import com.ruijing.registry.client.model.client.RegistryNodeQuery;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * RegistryService
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface RegistryService {

    Response<String> registry(RegistryNodeDO registryNode);

    Response<String> registry(List<RegistryNodeDO> registryNodeList);

    Response<String> remove(List<RegistryNodeDO> registryNodeList);

    Response<String> remove(RegistryNodeDO registryNode);

    Response<List<String>> discovery(RegistryNodeQuery query);

    Response<Map<String, List<String>>> discovery(Request<RegistryNodeQuery> request);

    DeferredResult<Response<String>> monitor(String biz, String env, List<String> keys);
}
