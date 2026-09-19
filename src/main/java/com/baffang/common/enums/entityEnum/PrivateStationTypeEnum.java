package com.baffang.common.enums.entityEnum;

/**
 * 私桩类型枚举
 */
public enum PrivateStationTypeEnum {
    /**
     * 普通私桩
     */
    NORMAL(0, "普通私桩"),
    
    /**
     * 专业私桩
     */
    PROFESSIONAL(1, "专业私桩");
    
    private final int code;
    private final String description;
    
    PrivateStationTypeEnum(int code, String description) {
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
    public static PrivateStationTypeEnum fromCode(int code) {
        for (PrivateStationTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid private station type code: " + code);
    }
}