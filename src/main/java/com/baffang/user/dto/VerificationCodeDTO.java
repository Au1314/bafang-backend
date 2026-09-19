package com.baffang.user.dto;

import lombok.Data;

/**
 * 验证码响应DTO
 */
@Data
public class VerificationCodeDTO {
    /**
     * 验证码有效期（秒）
     */
    private Integer expireTime;
    /**
     * 验证码
     */
    private String verifyCode;
}