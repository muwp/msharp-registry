package com.ruijing.registry.admin.data.model;

import java.util.Date;

/**
 * RegistryMessageMapper
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageQueueDO{");
        sb.append("id=").append(id);
        sb.append(", sequenceId=").append(sequenceId);
        sb.append(", biz='").append(biz).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
