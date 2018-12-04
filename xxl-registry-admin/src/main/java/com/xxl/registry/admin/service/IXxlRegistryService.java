package com.xxl.registry.admin.service;


import com.xxl.registry.admin.core.model.XxlRegistry;
import com.xxl.registry.admin.core.model.XxlRegistryData;
import com.xxl.registry.admin.core.result.ReturnT;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlRegistryService {

    // admin
    Map<String,Object> pageList(int start, int length, String biz, String env, String key);
    ReturnT<String> delete(int id);
    ReturnT<String> update(XxlRegistry xxlRegistry);
    ReturnT<String> add(XxlRegistry xxlRegistry);


    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broacase
     */
    ReturnT<String> registry(String accessToken, String biz, String env, List<XxlRegistryData> registryDataList);

    /**
     * remove registry-value, check update and broacase
     */
    ReturnT<String> remove(String accessToken, String biz, String env, List<XxlRegistryData> registryDataList);

    /**
     * discovery registry-data, read file
     */
    ReturnT<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys);

    /**
     * monitor update
     */
    DeferredResult<ReturnT<String>> monitor(String accessToken, String biz, String env, List<String> keys);

}
