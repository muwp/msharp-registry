package com.ruijing.registry.admin.data.model;

import java.util.Date;
import java.util.Objects;

/**
 * RegistryNodeDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryNodeDO {

    /**
     * id
     */
    private Long id;

    /**
     * 注册id
     */
    private Long registryId;

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
     * 版本号
     */
    private String version;

    /**
     * 注册Value
     */
    private String value;

    /**
     * 单结点元数据
     */
    private String meta;

    /**
     * 状态值
     */
    private Integer status;

    /**
     * 更新时间
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RegistryNodeDO that = (RegistryNodeDO) o;
        return Objects.equals(registryId, that.registryId) &&
                Objects.equals(appkey, that.appkey) &&
                Objects.equals(env, that.env) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryId, appkey, env, serviceName, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeDO{");
        sb.append("id=").append(id);
        sb.append(", registryId=").append(registryId);
        sb.append(", biz='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", meta='").append(meta).append('\'');
        sb.append(", key='").append(serviceName).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", status=").append(status);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
