package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.model.Response;
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

    Response<List<String>> discovery(String clientAppey, String biz, String env, String key);

    Response<Map<String, List<String>>> discovery(String biz, String env, List<String> keys);

    DeferredResult<Response<String>> monitor(String biz, String env, List<String> keys);
}
