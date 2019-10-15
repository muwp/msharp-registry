package com.ruijing.registry.admin.model;

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

    public static final int FORBIDDEN_CODE = 403;

    public static final int FAIL_CODE = 500;

    public static final Response<String> SUCCESS = new Response<>(null);

    public static final Response<String> FAIL = new Response<>(FAIL_CODE, null);

    public static final Response<String> FORBIDDEN = new Response<>(FORBIDDEN_CODE, null);

    private int code;

    private String msg;

    private T data;

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

    @Override
    public String toString() {
        return "Response [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }
}
