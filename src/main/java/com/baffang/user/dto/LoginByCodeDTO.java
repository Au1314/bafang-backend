package com.baffang.user.dto;

import lombok.Data;

/**
 * 用户验证码登录请求DTO
 */
@Data
public class LoginByCodeDTO {

    /**
     * 手机号
     * 必需
     */
    private String phone;

    /**
     * 验证码
     * 必需
     */
    private String code;
}
