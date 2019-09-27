package com.ruijing.registry.client.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xuxueli 2018-11-23
 */
public class MSharpRegistryData implements Serializable {

    /**
     * access Token
     */
    private String accessToken;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
        return "MSharpRegistryData{" +
                "biz='" + biz + '\'' +
                ", env='" + env + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
