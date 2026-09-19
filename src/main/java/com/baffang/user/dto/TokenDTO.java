package com.baffang.user.dto;

import lombok.Data;

/**
 * JWT令牌响应参数
 */
@Data
public class TokenDTO {
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间（毫秒）
     */
    private Long expiresIn;

    /**
     * 令牌类型
     */
    private String tokenType;
}
