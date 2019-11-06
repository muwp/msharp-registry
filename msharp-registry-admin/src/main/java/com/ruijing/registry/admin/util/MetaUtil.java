package com.ruijing.registry.admin.util;

import com.ruijing.registry.admin.meta.ServiceMeta;

public class MetaUtil {

    public static String convert(ServiceMeta meta) {
        return new StringBuilder()
                .append(meta.getAppkey())
                .append("#")
                .append(meta.getIp())
                .append("#")
                .append(meta.getPort())
                .append("#")
                .append(meta.getServiceName())
                .append("#")
                .append(meta.getWeight())
                .append("#")
                .append(meta.getStatus())
                .append("#")
                .append(meta.getVersion())
                .append("#")
                .append(meta.getGroup())
                .toString();
    }
}
