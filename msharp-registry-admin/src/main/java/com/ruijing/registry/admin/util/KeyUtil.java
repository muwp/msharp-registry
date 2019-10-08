package com.ruijing.registry.admin.util;

import com.ruijing.registry.common.http.Separator;

/**
 * KeyUtil
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public class KeyUtil {

    public static String getKey(String biz, String env, String key) {
        return biz + Separator.DOT + env + Separator.DOT + key;
    }
}
