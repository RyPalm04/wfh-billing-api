package com.palmer.wfhbillingapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> filterRegistrationBean(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
