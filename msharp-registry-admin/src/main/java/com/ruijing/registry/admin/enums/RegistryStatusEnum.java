package com.ruijing.registry.admin.enums;

/**
 * RegistryStatusEnum
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public enum RegistryStatusEnum {

    /**
     * 0-正常
     */
    NORMAL(0, "normal"),

    /**
     * 1-锁定
     */
    LOCKED(1, "locked"),

    /**
     * 2-禁用
     */
    FORBID(2, "forbid"),

    /**
     * 3-下线
     */
    OFFLINE(3, "offline");

    private final int code;

    private final String name;

    RegistryStatusEnum(int code, String name) {
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
