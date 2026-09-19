package com.baffang.admin.websocket;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket认证拦截器
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            
            if (token == null || token.isEmpty()) {
                log.warn("WebSocket连接失败: 缺少token");
                return false;
            }
            
            try {
                // 使用 JwtConstants.SECRET_KEY（和登录一致）
                String secretKey = JwtConstants.SECRET_KEY;
                
                // 验证token
                if (!JwtUtil.validateToken(secretKey, token)) {
                    log.warn("WebSocket连接失败: token无效或已过期");
                    return false;
                }
                
                // 获取用户名
                String username = JwtUtil.getUsernameFromToken(secretKey, token);
                
                // 尝试获取adminId（管理员）或userId（用户）
                Integer adminId = JwtUtil.getClaimFromToken(secretKey, token, "adminId", Integer.class);
                Integer userId = JwtUtil.getClaimFromToken(secretKey, token, "userId", Integer.class);
                
                // 如果没有adminId也没有userId，认证失败
                if (adminId == null && userId == null) {
                    log.warn("WebSocket连接失败: 无法获取adminId或userId");
                    return false;
                }
                
                // 将用户信息存入attributes
                if (adminId != null) {
                    attributes.put("adminId", adminId);
                    log.info("WebSocket连接认证成功(管理员): adminId={}, username={}", adminId, username);
                } else {
                    attributes.put("userId", userId);
                    log.info("WebSocket连接认证成功(用户): userId={}, username={}", userId, username);
                }
                
                attributes.put("username", username);
                attributes.put("token", token);
                
                return true;
                
            } catch (Exception e) {
                log.warn("WebSocket连接失败: token验证异常 - {}", e.getMessage());
                return false;
            }
        }
        
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后处理
    }
}
