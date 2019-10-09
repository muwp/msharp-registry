package com.ruijing.registry.admin.filter;

import com.google.gson.Gson;
import com.ruijing.registry.admin.annotation.PermissionLimit;
import com.ruijing.registry.admin.util.CookieUtil;
import com.ruijing.registry.common.env.Environment;
import com.ruijing.registry.common.http.HttpClientHelper;
import com.ruijing.registry.common.util.MD5Utils;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.net.URLEncoder;

/**
 * PermissionInterceptorAdapter
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Component
public class PermissionInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptorAdapter.class);

    public static final String LOGIN_IDENTITY_KEY = "MSHARP_REGISTRY_LOGIN_IDENTITY";

    private static String LOGIN_IDENTITY_TOKEN;

    private static String configCenter;

    private static final Gson gson = new Gson();

    static {
        configCenter = Environment.getConfigCenter();
        if (!configCenter.endsWith("/")) {
            configCenter = configCenter + "/";
        }
    }

    public static String getLoginIdentityToken() {
        return LOGIN_IDENTITY_TOKEN;
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    }

    public static boolean ifLogin(HttpServletRequest request) {
        //检测是否登录
        String token = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getHeader("token");
            if (null == token) {
                return false;
            }
        }
        return true;
    }

    public static boolean login(HttpServletResponse httpServletResponse, String username, String password, boolean ifRemember) {
        boolean result = false;
        //登录
        final String value;
        try {
            value = HttpClientHelper.INSTANCE.get(configCenter + "/pearl/user/login?userName=" + username + "&password=" + MD5Utils.md5(password), 10000);
            if (StringUtils.isNotBlank(value)) {
                RemoteResponse<String> response = gson.fromJson(value, RemoteResponse.class);
                //加上编码防止前端获取cookie中特殊符号为空格
                LOGIN_IDENTITY_TOKEN = URLEncoder.encode(response.data, String.valueOf(Charsets.toCharset("UTF-8")));
                if (null != response && response.code == RemoteResponse.SUCCESS_CODE) {
                    CookieUtil.set(httpServletResponse, LOGIN_IDENTITY_KEY, getLoginIdentityToken(), ifRemember);
                    result = true;
                }
            }
        } catch (Exception e) {
            logger.error("error login", e);
        }
        return result;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        if (!ifLogin(request)) {
            HandlerMethod method = (HandlerMethod) handler;
            final PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
            if (permission == null || permission.limit()) {
                response.sendRedirect(request.getContextPath() + "/toLogin");
                //request.getRequestDispatcher("/toLogin").forward(request, response);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    public static class RemoteResponse<T> implements Serializable {

        private static final int SUCCESS_CODE = 200;

        private int code;

        /**
         * 返回结果描述信息
         */
        private String msg;

        /**
         * 返回结果数据
         */
        private T data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
