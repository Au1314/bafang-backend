package com.baffang.user.dto;

import lombok.Data;

/**
 * 更新个人信息请求参数
 */
@Data
public class UpdateProfileDTO {
    private String name;
    private String email;
    private String avatar;
    private String signature;
}