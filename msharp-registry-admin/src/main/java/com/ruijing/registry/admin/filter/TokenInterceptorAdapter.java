package com.ruijing.registry.admin.filter;

import com.ruijing.fundamental.cat.Cat;
import com.ruijing.fundamental.cat.message.Transaction;
import com.ruijing.fundamental.common.builder.JsonObjectBuilder;
import com.ruijing.registry.admin.annotation.RegistryClient;
import com.ruijing.registry.admin.cache.TokenCache;
import com.ruijing.registry.admin.constants.ResponseConst;
import com.ruijing.registry.admin.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TokenInterceptorAdapter
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Service
public class TokenInterceptorAdapter extends HandlerInterceptorAdapter {

    @Autowired
    private TokenCache tokenCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        HandlerMethod method = (HandlerMethod) handler;
        final RegistryClient registryClient = method.getMethodAnnotation(RegistryClient.class);
        if (null == registryClient) {
            return super.preHandle(request, response, handler);
        }

        final String accessToken = request.getHeader("access_token");
        final String clientAppkey = request.getHeader("client_appkey");

        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(clientAppkey)) {
            return super.preHandle(request, response, handler);
        }

        final String serverToken = tokenCache.get(clientAppkey);
        if (StringUtils.isBlank(serverToken)) {
            return super.preHandle(request, response, handler);
        }

        if (serverToken.trim().equalsIgnoreCase(accessToken)) {
            return super.preHandle(request, response, handler);
        }

        Cat.logEvent("token[no_security]", JsonObjectBuilder.custom().put("clientAppkey", clientAppkey).put("clientAccessToken", accessToken).put("serviceAccessToken", serverToken).build().toString(), Transaction.ERROR, "");
        response.getOutputStream().print(JsonUtil.toJson(ResponseConst.FORBIDDEN));
        response.getOutputStream().flush();
        return false;
    }
}
