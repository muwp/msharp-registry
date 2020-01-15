package com.ruijing.registry.api.dto;

import java.io.Serializable;

/**
 * NodeMetaDTO
 * <p>
 * 注册中心元数据
 *
 * @author mwup
 * @version 1.0
 * @created 2018/12/03 12:32
 **/
public class NodeMetaDTO implements Serializable {

    public static final long serialVersionUID = 42L;

    /**
     * 服务端项目名称
     */
    private String appkey;

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 传输类型
     */
    private String transportType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本号
     */
    private String group;

    /**
     * 状态值
     * 0,
     * 1,正常有效状态
     * 2 被锁状
     */
    private Integer status;

    /**
     * 权重
     */
    private double weight = 10d;

    /**
     * jar接口的的版本号
     */
    private String jarVersion;

    public NodeMetaDTO() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public void setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NodeMetaDTO{");
        sb.append("appkey='").append(appkey).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", port=").append(port);
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", transportType='").append(transportType).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", status=").append(status);
        sb.append(", weight=").append(weight);
        sb.append(", jarVersion='").append(jarVersion).append('\'');
        sb.append('}');
        return sb.toString();
    }
}