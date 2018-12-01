package com.xxl.registry.client;

import com.xxl.registry.client.model.XxlRegistryParam;
import com.xxl.registry.client.util.BasicHttpUtil;
import com.xxl.registry.client.util.json.BasicJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * base util for registry
 *
 * @author xuxueli 2018-12-01 21:40:04
 */
public class XxlRegistryBaseClient {
    private static Logger logger = LoggerFactory.getLogger(XxlRegistryBaseClient.class);


    private String adminAddress;
    private List<String> adminAddressArr;
    private String biz;
    private String env;

    public XxlRegistryBaseClient(String adminAddress, String biz, String env) {
        this.adminAddress = adminAddress;
        this.biz = biz;
        this.env = env;

        // valid
        if (adminAddress==null || adminAddress.trim().length()==0) {
            throw new RuntimeException("xxl-registry adminAddress empty");
        }
        if (biz==null || biz.trim().length()<4 || biz.trim().length()>255) {
            throw new RuntimeException("xxl-registry biz empty Invalid[4~255]");
        }
        if (env==null || env.trim().length()<4 || env.trim().length()>255) {
            throw new RuntimeException("xxl-registry biz env Invalid[4~255]");
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
            if (registryParam.getKey()==null || registryParam.getKey().trim().length()<4 || registryParam.getKey().trim().length()>255) {
                throw new RuntimeException("xxl-registry registryParamList#key Invalid[4~255]");
            }
            if (registryParam.getValue()==null || registryParam.getValue().trim().length()<4 || registryParam.getValue().trim().length()>255) {
                throw new RuntimeException("xxl-registry registryParamList#value Invalid[4~255]");
            }
        }

        // pathUrl
        String pathUrl = "/api/registry/"+ biz +"/" + env;

        // param
        String paramsJson = BasicJson.toJson(registryParamList);

        // result
        Map<String, Object> respObj = requestAndValid(pathUrl, paramsJson, 5);
        return respObj!=null?true:false;
    }

    private Map<String, Object> requestAndValid(String pathUrl, String requestBody, int timeout){

        for (String adminAddressUrl: adminAddressArr) {
            String finalUrl = adminAddressUrl + pathUrl;

            // request
            String responseData = BasicHttpUtil.postBody(finalUrl, requestBody, timeout);
            if (responseData == null) {
                return null;
            }

            // parse resopnse
            Map<String, Object> resopnseMap = null;
            try {
                resopnseMap = BasicJson.parseMap(responseData);
            } catch (Exception e) { }


            // valid resopnse
            if (resopnseMap==null
                    || !resopnseMap.containsKey("code")
                    || !"200".equals(String.valueOf(resopnseMap.get("code")))
                    ) {
                logger.warn("XxlRegistryBaseClient response fail, responseData={}", responseData);
                return null;
            }

            return resopnseMap;
        }


        return null;
    }

    /**
     * remove
     *
     * @param registryParamList
     * @return
     */
    public boolean remove(List<XxlRegistryParam> registryParamList) {
        // valid
        if (registryParamList==null || registryParamList.size()==0) {
            throw new RuntimeException("xxl-registry registryParamList empty");
        }
        for (XxlRegistryParam registryParam: registryParamList) {
            if (registryParam.getKey()==null || registryParam.getKey().trim().length()<4 || registryParam.getKey().trim().length()>255) {
                throw new RuntimeException("xxl-registry registryParamList#key Invalid[4~255]");
            }
            if (registryParam.getValue()==null || registryParam.getValue().trim().length()<4 || registryParam.getValue().trim().length()>255) {
                throw new RuntimeException("xxl-registry registryParamList#value Invalid[4~255]");
            }
        }

        // pathUrl
        String pathUrl = "/api/remove/"+ biz +"/" + env;

        // param
        String paramsJson = BasicJson.toJson(registryParamList);

        // result
        Map<String, Object> respObj = requestAndValid(pathUrl, paramsJson, 5);
        return respObj!=null?true:false;
    }

    /**
     * discovery
     *
     * @param keys
     * @return
     */
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        // valid
        if (keys==null || keys.size()==0) {
            throw new RuntimeException("xxl-registry keys empty");
        }

        // pathUrl
        String pathUrl = "/api/discovery/"+ biz +"/" + env;

        // param
        String paramsJson = BasicJson.toJson(keys);

        // result
        Map<String, Object> respObj = requestAndValid(pathUrl, paramsJson, 5);

        // parse
        if (respObj!=null && respObj.containsKey("data")) {
            Map<String, TreeSet<String>> data = (Map<String, TreeSet<String>>) respObj.get("data");
            return data;
        }

        return null;
    }

    /**
     * discovery
     *
     * @param keys
     * @return
     */
    public boolean monitor(Set<String> keys) {
        // valid
        if (keys==null || keys.size()==0) {
            throw new RuntimeException("xxl-registry keys empty");
        }

        // pathUrl
        String pathUrl = "/api/monitor/"+ biz +"/" + env;

        // param
        String paramsJson = BasicJson.toJson(keys);

        // result
        Map<String, Object> respObj = requestAndValid(pathUrl, paramsJson, 60);
        return respObj!=null?true:false;
    }

}
