package com.baffang.config;

import com.baffang.common.constant.AuthConstants;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于从请求头中提取JWT令牌并验证
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserDetailsService adminDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
            UserDetailsService adminDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.adminDetailsService = adminDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中获取令牌
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                // 解析令牌并获取Claims
                Claims claims = jwtUtil.parseToken(JwtConstants.SECRET_KEY, token);
                // 从Claims中获取用户名
                String username = (String) claims.get(JwtConstants.USERNAME_CLAIM);
                // 从Claims中获取所属系统
                String authType = (String) claims.get(JwtConstants.AUTH_TYPE_CLAIM);

                // 如果用户名不为空、authType不为空且SecurityContext中没有认证信息
                if (StringUtils.hasText(username) && StringUtils.hasText(authType)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 根据authType选择合适的UserDetailsService
                    UserDetailsService detailsService = getDetailsServiceByAuthType(authType);
                    // 加载用户详细信息
                    UserDetails userDetails = detailsService.loadUserByUsername(username);
                    // 验证令牌是否有效
                    if (jwtUtil.validateToken(JwtConstants.SECRET_KEY, token, userDetails)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        // 设置认证详情
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // 将认证对象存储到SecurityContext中
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // 令牌验证失败，记录日志并继续过滤链
                logger.error("JWT令牌验证失败: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 根据请求路径选择合适的UserDetailsService
     */
    private UserDetailsService getUserDetailsService(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/admin/")) {
            return adminDetailsService;
        } else if (path.startsWith("/api/v1/user/")) {
            return userDetailsService;
        }
        // 默认使用用户服务
        return userDetailsService;
    }

    /**
     * 根据认证类型选择合适的UserDetailsService
     */
    private UserDetailsService getDetailsServiceByAuthType(String authType) {
        if ("admin".equals(authType)) {
            return adminDetailsService;
        } else {
            return userDetailsService;
        }
    }

    /**
     * 从请求头中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length()).trim();
        }
        return null;
    }
}
