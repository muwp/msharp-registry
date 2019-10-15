package com.ruijing.registry.admin.util;

import com.ruijing.fundamental.api.remote.RemoteResponse;
import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.common.env.Environment;
import com.ruijing.fundamental.common.util.JsonUtils;
import com.ruijing.fundamental.mhttp.common.HttpClientHelper;
import com.ruijing.registry.common.http.Separator;
import org.apache.commons.lang3.StringUtils;

/**
 * KeyUtil
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
public final class KeyUtil {


    public static String getKey(String biz, String env, String key) {
        return biz + Separator.DOT + env + Separator.DOT + key;
    }

    public static boolean validAppkey(String appkey) {
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

        return response.getData();
    }
}
