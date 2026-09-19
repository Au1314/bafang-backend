package com.baffang.user.dto;

import lombok.Data;

/**
 * 重置密码请求参数
 */
@Data
public class ResetPasswordDTO {
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 验证码
     */
    private String code;
    
    /**
     * 新密码
     */
    private String newpassword;
}