package com.ruijing.registry.admin.manager;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * RegistryCacheManager
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class RegistryCacheManager {

    private Map<String, Long> cache = new HashMap<>();

    public void add(String key, Long sequenceId) {
        cache.put(key, sequenceId);
    }

    public Long get(String key) {
        return cache.get(key);
    }
}
