package com.ruijing.registry.admin.data.model;

import java.util.Date;

/**
 * ClientNodeDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class ClientNodeDO {

    /**
     * id
     */
    private Long id;

    /**
     * registry id
     */
    private Long registryId;

    /**
     * 业务标识
     */
    private String clientAppkey;

    /**
     * 服务appkey
     */
    private String appkey;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * status
     */
    private Integer status;

    /**
     * 版本
     */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientNodeDO{");
        sb.append("id=").append(id);
        sb.append(", clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
