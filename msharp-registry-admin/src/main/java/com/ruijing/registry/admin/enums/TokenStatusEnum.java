package com.ruijing.registry.admin.enums;

/**
 * TokenStatusEnum
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public enum TokenStatusEnum {

    /**
     * 0-删除
     */
    DELETED(0, "deleted"),

    /**
     * 1-正常
     */
    NORMAL(1, "normal");

    private final int code;

    private final String name;

    TokenStatusEnum(int code, String name) {
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
        final StringBuilder sb = new StringBuilder("RegistryStatusEnum{");
        sb.append("code=").append(code);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
