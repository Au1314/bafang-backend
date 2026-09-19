package com.baffang.admin.dto;

import lombok.Data;

/**
 * 管理员登录请求参数
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
