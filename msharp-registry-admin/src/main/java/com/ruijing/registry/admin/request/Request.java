package com.ruijing.registry.admin.request;

import java.io.Serializable;
import java.util.List;

/**
 * Request
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class Request<T> implements Serializable {

    public static final long serialVersionUID = 42L;

    private List<T> list;

    private int version;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
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
        sb.append("list=").append(list);
        sb.append(", version=").append(version);
        sb.append('}');
        return sb.toString();
    }
}
