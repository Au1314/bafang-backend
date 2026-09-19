package com.baffang.common.enums.entityEnum;

/**
 * 私桩状态枚举
 */
public enum PrivateStationStatusEnum {
    /**
     * 空闲
     */
    IDLE(0, "空闲"),
    
    /**
     * 已预约
     */
    RESERVED(1, "已预约"),
    
    /**
     * 已充电
     */
    CHARGING(2, "已充电"),
    
    /**
     * 异常
     */
    ABNORMAL(3, "异常");
    
    private final int code;
    private final String description;
    
    PrivateStationStatusEnum(int code, String description) {
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
    public static PrivateStationStatusEnum fromCode(int code) {
        for (PrivateStationStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid private station status code: " + code);
    }
}