package com.ruijing.registry.admin.service;

import com.ruijing.registry.admin.data.model.RegistryDO;
import com.ruijing.registry.admin.data.model.RegistryNodeDO;
import com.ruijing.registry.admin.model.ReturnT;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface RegistryService {

    Map<String, Object> pageList(int start, int length, String biz, String env, String key);

    ReturnT<String> delete(int id);

    ReturnT<String> update(RegistryDO xxlRegistry);

    ReturnT<String> add(RegistryDO xxlRegistry);

    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broadcase
     *
     * @param accessToken      acc
     * @param registryDataList re
     * @return
     */
    ReturnT<String> registry(String accessToken, List<RegistryNodeDO> registryDataList);

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
     * @param registryData r
     * @return x
     */
    ReturnT<String> remove(String accessToken, RegistryNodeDO registryData);

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
