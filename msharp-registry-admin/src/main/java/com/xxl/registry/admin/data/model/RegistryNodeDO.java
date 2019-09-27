package com.xxl.registry.admin.data.model;

import java.util.Date;

/**
 * @author xuxueli 2018-11-23
 */
public class RegistryNodeDO {

    private int id;

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
     * 更新时间
     */
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryNodeDO{");
        sb.append("id=").append(id);
        sb.append(", biz='").append(biz).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
