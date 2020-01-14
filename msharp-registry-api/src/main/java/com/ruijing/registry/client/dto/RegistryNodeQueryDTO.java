package com.ruijing.registry.client.dto;

import java.io.Serializable;

/**
 * RegistryNodeQueryDTO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryNodeQueryDTO implements Serializable {

    public static final long serialVersionUID = 42L;

    /**
     * id
     */
    private Long id;

    /**
     * 客户端服务appKey
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
     * 传输方式
     */
    private String transportType;

    /**
     * 服务分组
     */
    private String group;

    /**
     * 服务订阅维度
     */
    private String scope;

    /**
     * 查询偏移量
     */
    private Long offset;

    /**
     * 每页大小
     */
    private Integer pageSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeQueryDTO{");
        sb.append("clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", appkey='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", scope='").append(scope).append('\'');
        sb.append('}');
        return sb.toString();
    }
}