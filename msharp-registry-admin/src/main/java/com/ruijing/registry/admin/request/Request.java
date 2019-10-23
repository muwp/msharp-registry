package com.ruijing.registry.admin.request;


import com.ruijing.registry.client.model.RegistryNode;

import java.io.Serializable;
import java.util.List;

/**
 * Request
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class Request implements Serializable {

    public static final long serialVersionUID = 42L;

    private List<RegistryNode> registryNodeList;

    private int version;

    public List<RegistryNode> getRegistryNodeList() {
        return registryNodeList;
    }

    public void setRegistryNodeList(List<RegistryNode> registryNodeList) {
        this.registryNodeList = registryNodeList;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("registryNodeList=").append(registryNodeList);
        sb.append(", version=").append(version);
        sb.append('}');
        return sb.toString();
    }
}
