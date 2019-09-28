package com.ruijing.registry.admin.manager;

import com.ruijing.registry.admin.model.ReturnT;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistryDeferredCacheManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryDeferredCacheManager {

    private Map<String, List<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    public void add(String key, DeferredResult deferredResult) {
        List<DeferredResult> deferredResults = registryDeferredResultMap.get(key);
        if (deferredResults == null) {
            deferredResults = new ArrayList<>();
            registryDeferredResultMap.put(key, deferredResults);
        }
        deferredResults.add(deferredResult);
    }

    public void remove(String key) {
        // brocast monitor client
        List<DeferredResult> deferredResultList = registryDeferredResultMap.get(key);
        if (CollectionUtils.isEmpty(deferredResultList)) {
            return;
        }
        registryDeferredResultMap.remove(key);
        for (DeferredResult deferredResult : deferredResultList) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Monitor key update."));
        }
    }

    public List<DeferredResult> get(String key) {
        return registryDeferredResultMap.get(key);
    }
}
