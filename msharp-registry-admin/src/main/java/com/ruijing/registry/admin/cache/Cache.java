package com.ruijing.registry.admin.cache;

import org.apache.commons.lang3.tuple.Triple;

/**
 * Cache
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public interface Cache<R> {

    R get(final Long id);

    R get(String appkey, String env, String serviceName);

    R get(final Triple<String, String, String> key);

    void put(final Triple<String, String, String> key, R R);

    boolean remove(final Long id);

    boolean remove(R R);

    int persist(R r);

    int refresh(R r);
}