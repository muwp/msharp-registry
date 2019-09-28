package com.ruijing.registry.admin.data.model;

import java.util.List;

/**
 * RegistryDO
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class RegistryDO {

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
     * 注册Value有效数据
     */
    private String data;

    /**
     * 版本
     */
    private String version;

    /**
     * 状态：0-正常、1-锁定、2-禁用,3-下线
     */
    private int status;

    /**
     * plugin(用于返回客户端结果)
     */
    private List<String> dataList;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryDO{");
        sb.append("id=").append(id);
        sb.append(", biz='").append(biz).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", status=").append(status);
        sb.append(", dataList=").append(dataList);
        sb.append('}');
        return sb.toString();
    }
}
