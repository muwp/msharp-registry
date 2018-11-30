package com.xxl.registry.client.test;

import com.xxl.registry.client.XxlRegistryClient;
import com.xxl.registry.client.model.XxlRegistryParam;

import java.util.ArrayList;
import java.util.List;

public class XxlRegistryClientTest {

    public static void main(String[] args) {
        XxlRegistryClient registryClient = new XxlRegistryClient("http://localhost:8080/xxl-registry-admin/", "xxl-rpc", "test");

        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        registryParamList.add(new XxlRegistryParam("k1", "v1"));
        registryParamList.add(new XxlRegistryParam("k2", "v2"));

        boolean ret = registryClient.registry(registryParamList);
        System.out.println(ret);
    }

}
