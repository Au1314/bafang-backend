package com.baffang.user.dto;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginDTO {

    /**
     * 手机号
     * 可选，11个字符
     */
    private String phone;

    /**
     * 密码
     * 必需，登录密码
     */
    private String password;

    /**
     * 用户名
     * 可选，与手机号二选一
     */
    private String username;
}