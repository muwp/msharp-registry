package com.ruijing.registry.admin.util;

import com.ruijing.registry.client.dto.ServiceNodeMetaDTO;

public class MetaUtil {

    public static String convert(ServiceNodeMetaDTO meta) {
        return new StringBuilder()
                .append(meta.getIp())
                .append(":")
                .append(meta.getPort())
                .toString();
    }
}
