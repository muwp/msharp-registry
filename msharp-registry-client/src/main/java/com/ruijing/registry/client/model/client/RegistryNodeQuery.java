package com.ruijing.registry.client.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * RegistryNodeQuery
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryNodeQuery implements Serializable {

    public static final long serialVersionUID = 42L;

    /**
     * 客户端服务appkey
     */
    private String clientAppkey;

    /**
     * 业务标识
     */
    @JsonProperty("biz")
    private String appkey;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 注册Key
     */
    @JsonProperty("key")
    private String serviceName;

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getClientAppkey() {
        return clientAppkey;
    }

    public void setClientAppkey(String clientAppkey) {
        this.clientAppkey = clientAppkey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeQuery{");
        sb.append("clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", appkey='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
