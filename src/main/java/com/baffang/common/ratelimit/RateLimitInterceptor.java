package com.baffang.common.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baffang.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 滑动窗口限流拦截器（真实可运营整改）
 * 键 = 客户端 IP + 接口方法；窗口内时间戳队列惰性清理；超过阈值返回 429。
 * 说明：当前为单机内存实现，与 WS 单机一致；演进项为 Redis ZSet 共享窗口。
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    /** 限流窗口：key(ip:method) -> 请求时间戳队列 */
    private final Map<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        String key = clientIp(request) + ":" + handlerMethod.getMethod().toGenericString();
        long now = System.currentTimeMillis();
        long windowStart = now - rateLimit.windowSeconds() * 1000L;

        Deque<Long> timestamps = windows.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            // 惰性清理：弹出窗口外的过期时间戳
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= rateLimit.limit()) {
                reject(response, rateLimit);
                return false;
            }
            timestamps.addLast(now);
        }
        return true;
    }

    private void reject(HttpServletResponse response, RateLimit rateLimit) throws Exception {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.error(429, "请求过于频繁，请 " + rateLimit.windowSeconds() + " 秒后再试")));
    }

    /** 优先取反向代理传递的真实 IP，回退 remoteAddr */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
