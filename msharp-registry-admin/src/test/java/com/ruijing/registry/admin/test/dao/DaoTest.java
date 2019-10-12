package com.ruijing.registry.admin.test.dao;

import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class DaoTest {

    public static void main(String[] args) throws Throwable {
        com.google.common.cache.Cache<Long, String> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
        cache.put(1L, "mwp");
        int i = 1;
        while (i++ < 1000) {
            String value = cache.getIfPresent(1L);
            if (i == 8) {
                cache.put(1L, "mwp");
            }
            cache.invalidate(1L);
            cache.invalidate(1L);
            System.out.println(value);
            Thread.sleep(1000);
        }
    }
}
