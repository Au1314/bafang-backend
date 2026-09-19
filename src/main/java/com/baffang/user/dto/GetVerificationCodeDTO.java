package com.baffang.user.dto;

import lombok.Data;

/**
 * 获取验证码请求DTO
 */
@Data
public class GetVerificationCodeDTO {
    /**
     * 有效手机号
     */
    private String phone;
}