package com.ruijing.registry.admin.filter;

import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Cors Filter
 * 允许跨域
 *
 * @author mwup
 * @version 1.0
 * @created 2018/9/4 17:03
 **/
@Order(1)
@WebFilter(filterName = "corsFilter", urlPatterns = "/*",asyncSupported = true)
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //解决与B端跨域问题
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, HaiYi-Access-Token, token");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        if ("OPTIONS".equals(((HttpServletRequest) request).getMethod())) {
            ((HttpServletResponse) response).setStatus(200);
            response.flushBuffer();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
