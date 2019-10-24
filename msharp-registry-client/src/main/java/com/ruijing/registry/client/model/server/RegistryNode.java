package com.ruijing.registry.client.model.server;

import java.io.Serializable;

/**
 * RegistryNodeDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryNode implements Serializable {

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

    /**
     * 注册Value
     */
    private String value;

    /**
     * 单个注册结点的元数据
     */
    private String meta;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClientAppkey() {
        return clientAppkey;
    }

    public void setClientAppkey(String clientAppkey) {
        this.clientAppkey = clientAppkey;
    }


    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNode{");
        sb.append("clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", biz='").append(biz).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", meta='").append(meta).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
