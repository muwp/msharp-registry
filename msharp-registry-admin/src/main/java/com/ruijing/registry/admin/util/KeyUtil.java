package com.ruijing.registry.admin.util;

import com.ruijing.fundamental.api.remote.RemoteResponse;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.env.Environment;
import com.ruijing.fundamental.common.util.JsonUtils;
import com.ruijing.fundamental.mhttp.common.HttpClientHelper;
import com.ruijing.registry.common.http.Separator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KeyUtil
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public final class KeyUtil {

    private static final Map<String, Boolean> VALID_KEY_MAP = new ConcurrentHashMap<>();

    public static String getKey(String appkey, String env, String serviceName) {
        return appkey + Separator.DOT + env + Separator.DOT + serviceName;
    }

    public static boolean validAppkey(String appkey) {
        if (VALID_KEY_MAP.containsKey(appkey)) {
            return VALID_KEY_MAP.get(appkey);
        }
        //check config center
        String configCenter = Environment.getConfigCenter();
        if (!configCenter.endsWith(com.ruijing.fundamental.mhttp.common.Separator.BACKLASH)) {
            configCenter = configCenter + com.ruijing.fundamental.mhttp.common.Separator.BACKLASH;
        }

        String result = null;
        try {
            result = HttpClientHelper.INSTANCE.get(configCenter + "pearl/app/valid?appkey=" + appkey, 10000);
        } catch (Exception ex) {
            Cat.logError("RemoteConfigManger", "valid", null, ex);
        }

        if (StringUtils.isBlank(result)) {
            return true;
        }

        final RemoteResponse<Boolean> response = JsonUtils.fromJson(result, RemoteResponse.class);
        if (null == response || !response.isSuccess()) {
            return true;
        }

        if (response.getData()) {
            VALID_KEY_MAP.put(appkey, response.getData());
        }
        return response.getData();
    }
}
