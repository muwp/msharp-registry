package com.ruijing.registry.admin.filter;

import com.ruijing.registry.admin.annotation.RegistryClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
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
@Component
public class TokenInterceptorAdapter extends HandlerInterceptorAdapter {

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
        return super.preHandle(request, response, handler);
    }
}
