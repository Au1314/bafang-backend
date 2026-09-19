package com.baffang.common.enums.entityEnum;

/**
 * 私桩预约状态枚举
 */
public enum PrivateStationReserveStatusEnum {
    /**
     * 待确认
     */
    PENDING_CONFIRMATION(0, "待确认"),
    
    /**
     * 已确认
     */
    CONFIRMED(1, "已确认"),
    
    /**
     * 已取消
     */
    CANCELED(2, "已取消");
    
    private final int code;
    private final String description;
    
    PrivateStationReserveStatusEnum(int code, String description) {
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
    public static PrivateStationReserveStatusEnum fromCode(int code) {
        for (PrivateStationReserveStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid private station reserve status code: " + code);
    }
}