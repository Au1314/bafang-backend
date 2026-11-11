package com.baffang.common.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解（真实可运营整改）
 * 按客户端 IP 维度的滑动窗口限流，用于登录/验证码/导出等易被滥用端点的防滥用与防爆破。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 窗口内允许的最大请求数
     */
    int limit();

    /**
     * 窗口长度（秒）
     */
    int windowSeconds() default 60;
}
