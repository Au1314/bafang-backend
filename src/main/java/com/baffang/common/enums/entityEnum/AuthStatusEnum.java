package com.baffang.common.enums.entityEnum;

/**
 * 认证状态枚举
 */
public enum AuthStatusEnum {
    /**
     * 未认证
     */
    UN_AUTHENTICATED(0, "未认证"),
    
    /**
     * 审核中
     */
    UNDER_REVIEW(1, "审核中"),
    
    /**
     * 已认证
     */
    AUTHENTICATED(2, "已认证"),
    
    /**
     * 认证失败
     */
    AUTHENTICATION_FAILED(3, "认证失败");
    
    private final int code;
    private final String description;
    
    AuthStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AuthStatusEnum fromCode(int code) {
        for (AuthStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid auth status code: " + code);
    }
}