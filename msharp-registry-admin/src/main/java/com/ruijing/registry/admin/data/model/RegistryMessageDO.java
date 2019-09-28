package com.ruijing.registry.admin.data.model;

import java.util.Date;

/**
 * RegistryMessageDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryMessageDO {

    private int id;

    /**
     * 消息类型：0-注册更新
     */
    private int type;

    /**
     * 消息内容
     */
    private String data;

    /**
     * 消息增加时间
     */
    private Date addTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryMessageDO{");
        sb.append("id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", data='").append(data).append('\'');
        sb.append(", addTime=").append(addTime);
        sb.append('}');
        return sb.toString();
    }
}
