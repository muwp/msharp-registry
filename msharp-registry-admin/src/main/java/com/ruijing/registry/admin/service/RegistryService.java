package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.model.ReturnT;
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

    ReturnT<String> delete(long id);

    ReturnT<String> update(RegistryDO registryDO);

    ReturnT<String> add(RegistryDO registryDO);

    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broadcase
     *
     * @param accessToken      acc
     * @param registryNodeList re
     * @return
     */
    ReturnT<String> registry(String accessToken, List<RegistryNodeDO> registryNodeList);

    /**
     * remove registry-value, check update and broacase
     *
     * @param accessToken      a
     * @param registryDataList r
     * @return x
     */
    ReturnT<String> remove(String accessToken, List<RegistryNodeDO> registryDataList);

    /**
     * remove registry-value, check update and broacase
     *
     * @param accessToken  a
     * @param registryNode r
     * @return x
     */
    ReturnT<String> remove(String accessToken, RegistryNodeDO registryNode);

    /**
     * discovery registry-data, read file
     *
     * @param accessToken a
     * @param biz         b
     * @param env         e
     * @param key         k
     * @return
     */
    ReturnT<List<String>> discovery(String accessToken, String biz, String env, String key);

    /**
     * discovery registry-data, read file
     *
     * @param accessToken a
     * @param biz         b
     * @param env         e
     * @param keys        k
     * @return
     */
    ReturnT<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys);

    /**
     * monitor update
     *
     * @param accessToken a
     * @param biz         b
     * @param env         e
     * @param keys        k
     * @return
     */
    DeferredResult<ReturnT<String>> monitor(String accessToken, String biz, String env, List<String> keys);
}
