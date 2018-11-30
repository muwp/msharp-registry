package com.xxl.registry.client.test;

import com.xxl.registry.client.XxlRegistryClient;
import com.xxl.registry.client.model.XxlRegistryParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class XxlRegistryClientTest {

    public static void main(String[] args) throws InterruptedException {
        XxlRegistryClient registryClient = new XxlRegistryClient("http://localhost:8080/xxl-registry-admin/", "xxl-rpc", "test");

        // registry test
        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        registryParamList.add(new XxlRegistryParam("service01", "address01"));
        registryParamList.add(new XxlRegistryParam("service02", "address02"));
        System.out.println("registry:" + registryClient.registry(registryParamList));
        TimeUnit.SECONDS.sleep(2);

        // discovery test
        Set<String> keys = new TreeSet<>();
        keys.add("service01");
        keys.add("service02");
        System.out.println("discovery:" + registryClient.discovery(keys));


        // remove test
        System.out.println("remove:" + registryClient.remove(registryParamList));
        TimeUnit.SECONDS.sleep(2);

        // discovery test
        System.out.println("discovery:" + registryClient.discovery(keys));

        TimeUnit.SECONDS.sleep(10);
        System.out.println("monitor...");
        registryClient.monitor(keys);
    }

}
