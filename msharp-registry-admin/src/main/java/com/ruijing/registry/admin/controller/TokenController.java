package com.ruijing.registry.admin.controller;

import com.ruijing.fundamental.api.remote.RemoteResponse;
import com.ruijing.fundamental.common.env.Environment;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.cache.TokenCache;
import com.ruijing.registry.admin.data.model.TokenDO;
import com.ruijing.registry.admin.util.KeyUtil;
import com.ruijing.registry.admin.util.RandNumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * PingController
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@RestController
@RequestMapping(value = "/token")
public class TokenController {

    private String test = "test";

    @Autowired
    private TokenCache tokenCache;

    @PermissionLimit(limit = false)
    @RequestMapping(value = "/get")
    @ResponseBody
    public RemoteResponse<String> token(@RequestParam(value = "appkey") String appkey) {
        if (test.equalsIgnoreCase(appkey)) {
            return RemoteResponse.custom().setData(false).setFailure("这是个测试appkey, 请输入正确的appkey").build();
        }

        if (!KeyUtil.validAppkey(appkey)) {
            return RemoteResponse.custom().setData(false).setFailure("appkey无效").build();
        }

        String token = tokenCache.syncGet(appkey);
        if (StringUtils.isNotBlank(token)) {
            return RemoteResponse.custom().setData(token).setSuccess().build();
        }

        token = RandNumUtil.genRandomNum();
        final TokenDO tokenDO = new TokenDO();
        tokenDO.setClientAppkey(appkey);
        tokenDO.setStatus(1);
        tokenDO.setUpdateTime(new Date());
        tokenDO.setCreateTime(new Date());
        tokenDO.setEnv(Environment.getEnv());
        tokenDO.setToken(token);
        final int size = tokenCache.persist(tokenDO);
        if (size > 0) {
            return RemoteResponse.custom().setData(token).setSuccess().build();
        } else {
            return RemoteResponse.custom().setData(null).setFailure("fail").build();
        }
    }
}