package com.baffang.user.dto;

import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordDTO {

    /**
     * 旧密码
     * 必需，6-20个字符
     */
    private String oldPassword;

    /**
     * 新密码
     * 必需，6-20个字符
     */
    private String newPassword;
}
