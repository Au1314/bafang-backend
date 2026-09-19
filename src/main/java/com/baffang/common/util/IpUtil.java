package com.baffang.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 */
public class IpUtil {

    /**
     * 获取客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (isBlank(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
