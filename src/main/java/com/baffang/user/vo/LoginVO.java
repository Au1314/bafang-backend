package com.baffang.user.vo;

import lombok.Data;

/**
 * 用户登录响应VO
 */
@Data
public class LoginVO {

    /**
     * 用户ID（关联user_info.id）
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录token
     */
    private String token;
}