package com.baffang.common.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * 标注在需要记录日志的方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作行为
     */
    String action() default "";

    /**
     * 是否记录请求参数（敏感接口可设为false）
     */
    boolean saveParams() default true;

    /**
     * 是否记录返回结果（大数据量接口可设为false）
     */
    boolean saveResult() default true;
}
