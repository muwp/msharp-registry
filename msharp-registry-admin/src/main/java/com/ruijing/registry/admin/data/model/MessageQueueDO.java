package com.ruijing.registry.admin.data.model;

import java.util.Date;

/**
 * MessageQueueDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class MessageQueueDO {

    private Long id;

    /**
     * 注册Value有效数据
     */
    private Long sequenceId;

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
     * 版本
     */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageQueueDO{");
        sb.append("id=").append(id);
        sb.append(", sequenceId=").append(sequenceId);
        sb.append(", appkey='").append(appkey).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
