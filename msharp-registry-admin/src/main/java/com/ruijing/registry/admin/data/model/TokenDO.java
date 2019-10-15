package com.ruijing.registry.admin.data.model;

import java.util.Date;

/**
 * token dto
 *
 * @author mwup
 * @version 1.0
 * @created 2018/9/4 17:03
 **/
public class TokenDO {

    /**
     * id
     */
    private Long id;

    /**
     * client 应用名称
     */
    private String clientAppkey;

    /**
     * 关键字
     */
    private String token;

    /**
     * 环境
     */
    private String env;

    /**
     * 状态值
     */
    private Integer status;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenDO{");
        sb.append("id=").append(id);
        sb.append(", clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}