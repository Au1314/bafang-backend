package com.baffang.framework.permission.annotation;

import java.lang.annotation.*;

/**
 * 权限控制注解
 * 用于标记需要权限验证的方法或类
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 需要的权限编码
     */
    String value() default "";

    /**
     * 是否需要登录
     */
    boolean needLogin() default true;

    /**
     * 是否需要权限验证
     */
    boolean needPermission() default true;

    /**
     * 权限验证失败时的错误信息
     */
    String message() default "权限不足";
}
