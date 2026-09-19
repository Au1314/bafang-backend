package com.baffang.common.enums.entityEnum;

/**
 * 开放状态枚举
 */
public enum OpenStatusEnum {
    /**
     * 关闭
     */
    CLOSED(0, "关闭"),
    
    /**
     * 开启
     */
    OPEN(1, "开启");
    
    private final int code;
    private final String description;
    
    OpenStatusEnum(int code, String description) {
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
    public static OpenStatusEnum fromCode(int code) {
        for (OpenStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid open status code: " + code);
    }
}