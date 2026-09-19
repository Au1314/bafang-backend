package com.baffang.user.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDTO {

    /**
     * 手机号
     * 必需，11个字符
     */
    private String phone;

    /**
     * 密码
     * 必需，8-20个字符
     */
    private String password;

    /**
     * 验证码
     * 必需，6个字符
     */
    private String verifyCode;
}
