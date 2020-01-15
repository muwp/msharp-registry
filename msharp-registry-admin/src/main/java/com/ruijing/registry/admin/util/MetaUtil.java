package com.ruijing.registry.admin.util;

import com.ruijing.registry.api.dto.NodeMetaDTO;

public class MetaUtil {

    public static String convert(NodeMetaDTO meta) {
        return new StringBuilder()
                .append(meta.getIp())
                .append(":")
                .append(meta.getPort())
                .toString();
    }
}
