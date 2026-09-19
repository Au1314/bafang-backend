package com.baffang.common.util;

import com.baffang.common.constant.JwtConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

    /**
     * 生成JWT令牌
     * 
     * @param secretKey      密钥
     * @param expirationTime 过期时间（毫秒）
     * @param claims         自定义声明
     * @return JWT令牌
     */
    public static String generateToken(String secretKey, long expirationTime, Map<String, Object> claims) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 解析后的JWT声明
     */
    public static Claims parseToken(String secretKey, String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            // 去除令牌中的空格，防止解析失败
            String trimmedToken = token.trim();

            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(trimmedToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT令牌无效: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 验证JWT令牌
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 是否有效
     */
    public static boolean validateToken(String secretKey, String token) {
        try {
            parseToken(secretKey, token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证JWT令牌（与用户详情比较）
     * 
     * @param secretKey   密钥
     * @param token       JWT令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public static boolean validateToken(String secretKey, String token, UserDetails userDetails) {
        try {
            Claims claims = parseToken(secretKey, token);
            String username = (String) claims.get(JwtConstants.USERNAME_CLAIM);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(secretKey, token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据用户详情创建认证对象
     * 
     * @param userDetails 用户详情
     * @return 认证对象
     */
    public static Authentication createAuthentication(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return authentication;
    }

    /**
     * 从JWT令牌中获取指定声明
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @param claimName 声明名称
     * @param <T>       声明类型
     * @return 声明值
     */
    public static <T> T getClaimFromToken(String secretKey, String token, String claimName, Class<T> clazz) {
        Claims claims = parseToken(secretKey, token);
        return claims.get(claimName, clazz);
    }

    /**
     * 获取令牌过期时间
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String secretKey, String token) {
        Claims claims = parseToken(secretKey, token);
        return claims.getExpiration();
    }

    /**
     * 检查令牌是否过期
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 是否过期
     */
    public static boolean isTokenExpired(String secretKey, String token) {
        Date expirationDate = getExpirationDateFromToken(secretKey, token);
        return expirationDate.before(new Date());
    }

    /**
     * 从JWT令牌中获取权限列表
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 权限列表
     */
    public static List<String> getPermissionsFromToken(String secretKey, String token) {
        Claims claims = parseToken(secretKey, token);
        Object permissionsObj = claims.get(JwtConstants.PERMISSIONS_CLAIM);
        if (permissionsObj instanceof List) {
            return (List<String>) permissionsObj;
        }
        return new ArrayList<>();
    }

    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String secretKey, String token) {
        return getClaimFromToken(secretKey, token, JwtConstants.USER_ID_CLAIM, Long.class);
    }

    /**
     * 从JWT令牌中获取用户ID（Integer类型）
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 用户ID
     */
    public static Integer getUserIdIntFromToken(String secretKey, String token) {
        Long userId = getClaimFromToken(secretKey, token, JwtConstants.USER_ID_CLAIM, Long.class);
        return userId != null ? userId.intValue() : null;
    }

    /**
     * 从JWT令牌中获取用户名
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String secretKey, String token) {
        return getClaimFromToken(secretKey, token, JwtConstants.USERNAME_CLAIM, String.class);
    }

    /**
     * 从JWT令牌中获取角色
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 角色
     */
    public static Integer getRoleFromToken(String secretKey, String token) {
        return getClaimFromToken(secretKey, token, JwtConstants.ROLE_CLAIM, Integer.class);
    }

    /**
     * 从JWT令牌中获取认证类型（所属系统）
     * 
     * @param secretKey 密钥
     * @param token     JWT令牌
     * @return 认证类型
     */
    public static String getAuthTypeFromToken(String secretKey, String token) {
        return getClaimFromToken(secretKey, token, JwtConstants.AUTH_TYPE_CLAIM, String.class);
    }
}