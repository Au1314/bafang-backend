package com.baffang.common.enums.entityEnum;

/**
 * 紧急呼叫类型枚举
 */
public enum EmergencyCallTypeEnum {
    /**
     * 呼叫拖车
     */
    TOW_TRUCK(0, "呼叫拖车"),
    
    /**
     * 呼叫充电桩
     */
    CHARGE_STATION(1, "呼叫充电桩"),
    
    /**
     * 其他呼叫
     */
    OTHER(2, "其他呼叫");
    
    private final int code;
    private final String description;
    
    EmergencyCallTypeEnum(int code, String description) {
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
    public static EmergencyCallTypeEnum fromCode(int code) {
        for (EmergencyCallTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid emergency call type code: " + code);
    }
}