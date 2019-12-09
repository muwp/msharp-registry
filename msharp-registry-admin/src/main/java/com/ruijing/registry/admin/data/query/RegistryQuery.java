package com.ruijing.registry.admin.data.query;

/**
 * RegistryQuery
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryQuery {

    /**
     * id
     */
    private Long id;

    /**
     * clientAppkey
     */
    private String clientAppkey;

    /**
     * 业务标识(服务端appkey)
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

    public String getClientAppkey() {
        return clientAppkey;
    }

    public void setClientAppkey(String clientAppkey) {
        this.clientAppkey = clientAppkey;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeQuery{");
        sb.append("id=").append(id);
        sb.append(", biz='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(serviceName).append('\'');
        sb.append(", offset=").append(offset);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }
}
