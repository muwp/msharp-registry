package com.ruijing.registry.admin.data.model;

import java.util.Date;
import java.util.Objects;

/**
 * ClientNodeDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class ClientNodeDO {

    private Long id;

    /**
     * 业务标识
     */
    private String clientAppkey;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 服务名称
     */
    private String serviceName;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientNodeDO that = (ClientNodeDO) o;
        return Objects.equals(clientAppkey, that.clientAppkey) &&
                Objects.equals(env, that.env) &&
                Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientAppkey, env, serviceName);
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
