package com.ruijing.registry.admin.data.query;

/**
 * RegistryNodeQueryDTO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryNodeQuery {

    /**
     * id
     */
    private Long id;

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
     * 注册id
     */
    private Long registryId;

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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
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

    public Long getRegistryId() {
        return registryId;
    }

    public void setRegistryId(Long registryId) {
        this.registryId = registryId;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeQueryDTO{");
        sb.append("id=").append(id);
        sb.append(", biz='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(serviceName).append('\'');
        sb.append(", registryId=").append(registryId);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", offset=").append(offset);
        sb.append('}');
        return sb.toString();
    }
}
