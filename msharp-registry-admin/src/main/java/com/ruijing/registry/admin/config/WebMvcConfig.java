package com.ruijing.registry.admin.config;

import com.ruijing.registry.admin.filter.CookieInterceptor;
import com.ruijing.registry.admin.filter.MPermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * WebMvcConfig
 *
 * @author mwup
 * @version 1.0
 * @created 2019/07/23 17:03
 **/
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Resource
    private MPermissionInterceptor mPermissionInterceptor;

    @Resource
    private CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mPermissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}