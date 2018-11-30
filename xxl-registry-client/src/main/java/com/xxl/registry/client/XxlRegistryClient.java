package com.xxl.registry.client;

import com.xxl.registry.client.model.XxlRegistryParam;
import com.xxl.registry.client.util.BasicHttpUtil;
import com.xxl.registry.client.util.json.BasicJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class XxlRegistryClient {
    private static Logger logger = LoggerFactory.getLogger(XxlRegistryClient.class);


    private String adminAddress;
    private List<String> adminAddressArr;
    private String biz;
    private String env;

    public XxlRegistryClient(String adminAddress, String biz, String env) {
        this.adminAddress = adminAddress;
        this.biz = biz;
        this.env = env;

        // valid
        if (adminAddress==null || adminAddress.trim().length()==0) {
            throw new RuntimeException("xxl-registry adminAddress empty");
        }
        if (env==null || env.trim().length()==0) {
            throw new RuntimeException("xxl-registry env empty");
        }
        if (biz==null || biz.trim().length()==0) {
            throw new RuntimeException("xxl-registry biz empty");
        }
        if (env==null || env.trim().length()==0) {
            throw new RuntimeException("xxl-registry env empty");
        }

        // parse
        adminAddressArr = new ArrayList<>();
        if (adminAddress.contains(",")) {
            adminAddressArr.addAll(Arrays.asList(adminAddress.split(",")));
        } else {
            adminAddressArr.add(adminAddress);
        }

    }

    /**
     * registry
     *
     * @param registryParamList
     * @return
     */
    public boolean registry(List<XxlRegistryParam> registryParamList){

        // valid
        if (registryParamList==null || registryParamList.size()==0) {
            throw new RuntimeException("xxl-registry registryParamList empty");
        }
        for (XxlRegistryParam registryParam: registryParamList) {
            if (registryParam.getKey()==null || registryParam.getKey().trim().length()==0) {
                throw new RuntimeException("xxl-registry registryParamList#key empty");
            }
            if (registryParam.getValue()==null || registryParam.getValue().trim().length()==0) {
                throw new RuntimeException("xxl-registry registryParamList#value empty");
            }
        }

        // pathUrl
        String pathUrl = "/api/registry/"+ biz +"/" + env;

        // param
        String paramsJson = BasicJson.toJson(registryParamList);


        Map<String, Object> respObj = requestAndValid(pathUrl, paramsJson);
        if (respObj == null) {
            return false;
        }
        if ("200".equals(String.valueOf(respObj.get("code")))) {
            return true;
        } else {
            logger.warn("XxlRegistryClient registry fail, msg={}", respObj.get("msg"));
            return false;
        }
    }

    private Map<String, Object> requestAndValid(String pathUrl, String requestBody){

        for (String adminAddressUrl: adminAddressArr) {
            String finalUrl = adminAddressUrl + pathUrl;

            // request
            String responseData = BasicHttpUtil.postBody(finalUrl, requestBody, 10);
            if (responseData == null) {
                return null;
            }

            // parse resopnse
            Map<String, Object> resopnseMap = null;
            try {
                resopnseMap = BasicJson.parseMap(responseData);
            } catch (Exception e) { }


            // valid resopnse
            if (resopnseMap==null || !resopnseMap.containsKey("code")) {
                logger.warn("XxlRegistryClient response parse fail, responseData={}", responseData);
                return null;
            }

            return resopnseMap;
        }


        return null;
    }

    public boolean remove(List<XxlRegistryParam> xxlRegistryParamList) {
        return false;
    }

    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        return null;
    }

    public boolean monitor(Set<String> keys) {
        return false;
    }

}
