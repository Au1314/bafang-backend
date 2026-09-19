package com.baffang.config;

import com.baffang.framework.permission.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限拦截器配置类
 * 用于将PermissionInterceptor注册到Spring的拦截器链中
 */
@Configuration
public class PermissionInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册权限拦截器
        registry.addInterceptor(permissionInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要拦截的请求
                .excludePathPatterns(
                        // 登录注册相关
                        "/api/v1/user/login",
                        "/api/v1/user/register",
                        "/api/v1/admin/login",
                        "/api/v1/reset-password",
                        "/api/v1/refresh-token",
                        
                        // Swagger相关
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        // 静态资源
                        "/static/**",
                        "/favicon.ico");
    }
}
