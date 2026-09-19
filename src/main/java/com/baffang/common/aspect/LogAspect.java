package com.baffang.common.aspect;

import com.baffang.common.annotation.SystemLog;
import com.baffang.common.constant.JwtConstants;
import com.baffang.admin.mapper.SystemLogMapper;
import com.baffang.common.util.IpUtil;
import com.baffang.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统日志切面
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 切点：所有带 @SystemLog 注解的方法
     */
    @Pointcut("@annotation(com.baffang.common.annotation.SystemLog)")
    public void logPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemLog systemLogAnnotation = method.getAnnotation(SystemLog.class);

        // 构建日志对象
        com.baffang.common.entity.SystemLog systemLog = new com.baffang.common.entity.SystemLog();
        systemLog.setModule(systemLogAnnotation.module());
        systemLog.setAction(systemLogAnnotation.action());
        systemLog.setCreateTime(new Date());

        // 填充请求信息
        if (request != null) {
            systemLog.setMethod(request.getMethod());
            systemLog.setUrl(request.getRequestURI());
            systemLog.setIp(IpUtil.getClientIp(request));
            systemLog.setUserAgent(truncate(request.getHeader("User-Agent"), 500));

            // 解析用户/管理员ID
            parseUserInfo(request, systemLog);
        }

        // 记录请求参数
        if (systemLogAnnotation.saveParams()) {
            try {
                String params = getParams(joinPoint, request);
                systemLog.setParams(truncate(params, 65535));
            } catch (Exception e) {
                log.warn("记录请求参数失败: {}", e.getMessage());
            }
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 记录成功
            systemLog.setStatus(1);

            // 记录返回结果
            if (systemLogAnnotation.saveResult() && result != null) {
                try {
                    String resultJson = objectMapper.writeValueAsString(result);
                    systemLog.setResult(truncate(resultJson, 65535));
                } catch (Exception e) {
                    log.warn("序列化返回结果失败: {}", e.getMessage());
                }
            }

            return result;

        } catch (Throwable e) {
            // 记录失败
            systemLog.setStatus(0);
            systemLog.setErrorMsg(truncate(e.getMessage(), 65535));
            throw e;

        } finally {
            // 记录执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            systemLog.setExecutionTime((int) executionTime);

            // 异步保存日志
            saveLogAsync(systemLog);
        }
    }

    /**
 * 解析用户信息（从Token中）
 */
private void parseUserInfo(HttpServletRequest request, com.baffang.common.entity.SystemLog systemLog) {
    try {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return;
        }
        
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "").trim();
        
        // 解析 Token
        io.jsonwebtoken.Claims claims = JwtUtil.parseToken(JwtConstants.SECRET_KEY, token);
        
        // 打印调试信息（上线后删除）
        log.debug("Token Claims: {}", claims);
        
        // 获取认证类型
        String authType = claims.get(JwtConstants.AUTH_TYPE_CLAIM, String.class);
        
        if ("admin".equals(authType)) {
            // ✅ 管理员 Token - 获取 adminId
            Object adminIdObj = claims.get("adminId");
            if (adminIdObj instanceof Number) {
                systemLog.setAdminId(((Number) adminIdObj).intValue());
            } else {
                // 兼容：尝试从 userId 获取
                Object userIdObj = claims.get(JwtConstants.USER_ID_CLAIM);
                if (userIdObj instanceof Number) {
                    systemLog.setAdminId(((Number) userIdObj).intValue());
                }
            }
        } else {
            // ✅ 普通用户 Token - 获取 userId
            Object userIdObj = claims.get(JwtConstants.USER_ID_CLAIM);
            if (userIdObj instanceof Number) {
                systemLog.setUserId(((Number) userIdObj).intValue());
            }
        }
        
    } catch (Exception e) {
        log.warn("解析用户信息失败: {}", e.getMessage());
    }
}


    /**
     * 获取请求参数
     */
    private String getParams(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Exception {
        Map<String, Object> params = new HashMap<>();

        // 1. URL参数
        if (request != null && request.getParameterMap() != null) {
            request.getParameterMap().forEach((key, value) -> {
                if (value != null && value.length == 1) {
                    params.put(key, value[0]);
                } else {
                    params.put(key, value);
                }
            });
        }

        // 2. 方法参数（排除特殊类型）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            Object arg = args[i];
            if (arg != null && !isExcludedType(arg)) {
                // 脱敏处理
                if (paramNames[i].toLowerCase().contains("password") ||
                    paramNames[i].toLowerCase().contains("token") ||
                    paramNames[i].toLowerCase().contains("authorization")) {
                    params.put(paramNames[i], "******");
                } else {
                    params.put(paramNames[i], arg);
                }
            }
        }

        return objectMapper.writeValueAsString(params);
    }

    /**
     * 判断是否为需要排除的类型
     */
    private boolean isExcludedType(Object arg) {
        return arg instanceof HttpServletRequest ||
        arg instanceof jakarta.servlet.http.HttpServletResponse ||
               arg instanceof org.springframework.web.multipart.MultipartFile;
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }

    /**
     * 异步保存日志
     */
    @Async
    public void saveLogAsync(com.baffang.common.entity.SystemLog systemLog) {
        try {
            systemLogMapper.insert(systemLog);
        } catch (Exception e) {
            log.error("保存系统日志失败: {}", e.getMessage());
        }
    }
}
