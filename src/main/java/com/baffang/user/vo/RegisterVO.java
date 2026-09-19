package com.baffang.user.vo;

import lombok.Data;

/**
 * 用户注册响应VO
 */
@Data
public class RegisterVO {
    
    /**
     * 新增用户ID（关联user_info.id）
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 创建时间（对应user_info.create_time）
     */
    private String createTime;
}
