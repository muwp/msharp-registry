package com.ruijing.registry.admin.filter;

import com.ruijing.fundamental.cat.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot AT Filter
 *
 * @author mwup
 * @version 1.0
 * @created 2018/10/21 13:51
 **/
@Configuration
public class CatFilterConfigure {

    @Bean
    public FilterRegistrationBean catFilter() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        final CatFilter filter = new CatFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);
        return registration;
    }
}
