package com.ruijing.registry.client.response;

import java.io.Serializable;

/**
 * Response
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class Response<T> implements Serializable {

    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;

    private int code;

    private String msg;

    private T data;

    /**
     * 版本号
     * 默认为 0
     */
    private int version;

    public Response() {
    }

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(T data) {
        this.code = SUCCESS_CODE;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Response [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }
}
