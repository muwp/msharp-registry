package com.ruijing.registry.client.model.v2;

import java.io.Serializable;

/**
 * RegistryNode
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
    private String appkey;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 注册Key
     */
    private String serviceName;

    /**
     * 注册Value
     */
    private String value;

    /**
     * 版本号
     */
    private String version;

    /**
     * 单个注册结点的元数据
     */
    private String meta;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNode{");
        sb.append("clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", appkey='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", meta='").append(meta).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
