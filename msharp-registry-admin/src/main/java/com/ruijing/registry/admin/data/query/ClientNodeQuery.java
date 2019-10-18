package com.ruijing.registry.admin.data.query;

/**
 * ClientNodeQuery
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class ClientNodeQuery {

    /**
     * id
     */
    private Long id;

    /**
     * 注册Key
     */
    private String clientAppkey;

    /**
     * 业务标识
     */
    private String serviceName;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 查询偏移量
     */
    private Long offset;

    /**
     * 每页大小
     */
    private Integer pageSize;

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

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientNodeQuery{");
        sb.append("id=").append(id);
        sb.append(", clientAppkey='").append(clientAppkey).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", offset=").append(offset);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }
}
