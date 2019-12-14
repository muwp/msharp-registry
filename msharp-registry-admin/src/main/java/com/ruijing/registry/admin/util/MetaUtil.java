package com.ruijing.registry.admin.util;

import com.ruijing.registry.admin.meta.ServiceMeta;

public class MetaUtil {

    public static String convert(ServiceMeta meta) {
        return new StringBuilder()
                .append(meta.getIp())
                .append(":")
                .append(meta.getPort())
                .toString();
    }
}
