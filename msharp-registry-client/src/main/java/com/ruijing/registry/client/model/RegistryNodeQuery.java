package com.ruijing.registry.client.model;

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
    private String biz;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 注册Key
     */
    private String key;

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClientAppkey() {
        return clientAppkey;
    }

    public void setClientAppkey(String clientAppkey) {
        this.clientAppkey = clientAppkey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNode{");
        sb.append("clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", biz='").append(biz).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
