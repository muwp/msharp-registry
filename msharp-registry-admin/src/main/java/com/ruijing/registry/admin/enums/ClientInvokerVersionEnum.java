package com.ruijing.registry.admin.enums;

/**
 * ClientInvokerVersionEnum
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public enum ClientInvokerVersionEnum {

    /**
     * 0-删除
     */
    VERSION_0(0, "0"),

    /**
     * 1-正常
     */
    VERSION_2(2, "2");

    private final int code;

    private final String name;

    ClientInvokerVersionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientInvokerVersionEnum{");
        sb.append("code=").append(code);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
