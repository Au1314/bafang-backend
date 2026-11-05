package com.baffang.config;

import com.baffang.common.constant.JwtConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * JWT 密钥配置（安全整改）
 * 密钥只允许通过配置注入（环境变量 JWT_SECRET 或配置项 jwt.secret），
 * 应用启动时校验并写入 JwtConstants.SECRET_KEY；未配置或长度不足直接启动失败（fail-closed）。
 */
@Configuration
public class JwtKeyConfig {

    @Bean
    public InitializingBean jwtKeyInitializer(@Value("${jwt.secret:}") String secret) {
        return () -> {
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException(
                        "缺少 JWT 签名密钥：请通过环境变量 JWT_SECRET 或配置项 jwt.secret 注入至少 32 字节的随机密钥");
            }
            byte[] keyBytes = secret.trim().getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "jwt.secret 长度不足：HS256 要求至少 32 字节（当前 " + keyBytes.length + " 字节）");
            }
            JwtConstants.SECRET_KEY = secret.trim();
        };
    }
}
