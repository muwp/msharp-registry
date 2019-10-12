package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryDO;
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

    Map<String, Object> pageList(int start, int length, String biz, String env, String key);

    Response<String> delete(long id);

    Response<String> update(RegistryDO registryDO);

    Response<String> add(RegistryDO registryDO);

    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broadcase
     *
     * @param registryNodeList re
     * @return
     */
    Response<String> registry(List<RegistryNodeDO> registryNodeList);

    /**
     * remove registry-value, check update and broacase
     *
     * @param registryDataList r
     * @return x
     */
    Response<String> remove(List<RegistryNodeDO> registryDataList);

    /**
     * remove registry-value, check update and broacase
     *
     * @param registryNode
     * @return x
     */
    Response<String> remove(RegistryNodeDO registryNode);

    /**
     * discovery registry-data, read file
     *
     * @param biz b
     * @param env e
     * @param key k
     * @return
     */
    Response<List<String>> discovery(String biz, String env, String key);

    /**
     * discovery registry-data, read file
     *
     * @param biz  b
     * @param env  e
     * @param keys k
     * @return
     */
    Response<Map<String, List<String>>> discovery(String biz, String env, List<String> keys);

    /**
     * monitor update
     *
     * @param biz         b
     * @param env         e
     * @param keys        k
     * @return
     */
    DeferredResult<Response<String>> monitor(String biz, String env, List<String> keys);
}
