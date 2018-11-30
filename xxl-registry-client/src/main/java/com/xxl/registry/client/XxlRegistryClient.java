package com.xxl.registry.client;

import com.xxl.registry.client.model.XxlRegistryParam;

import java.util.*;

public class XxlRegistryClient {

    private String adminAddress;
    private List<String> adminAddressArr;
    private String biz;
    private String env;

    public XxlRegistryClient(String biz, String env) {
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
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");

        for (int i = 0; i < registryParamList.size(); i++) {
            stringBuffer.append("{");
            stringBuffer.append("\"key\":").append("\""+ registryParamList.get(i).getKey() +"\",");
            stringBuffer.append("\"value\":").append("\""+ registryParamList.get(i).getValue() +"\",");
            stringBuffer.append("}");
            if (i == registryParamList.size()-1) {
                stringBuffer.append(",");
            }
        }


        stringBuffer.append("]");
        String paramsJson = stringBuffer.toString();


        for (String adminAddressUrl: adminAddressArr) {
            String finalUrl = adminAddressUrl + pathUrl;
            Map<String, Object> respObj = null;//getAndValid(url, 10);
            return respObj!=null?true:false;
        }
        return false;
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
