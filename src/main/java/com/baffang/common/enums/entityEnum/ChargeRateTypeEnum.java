package com.baffang.common.enums.entityEnum;

/**
 * 充电桩评价类型枚举
 */
public enum ChargeRateTypeEnum {
    /**
     * 公桩评价
     */
    PUBLIC_STATION(0, "公桩评价"),
    
    /**
     * 私桩评价
     */
    PRIVATE_STATION(1, "私桩评价");
    
    private final int code;
    private final String description;
    
    ChargeRateTypeEnum(int code, String description) {
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
    public static ChargeRateTypeEnum fromCode(int code) {
        for (ChargeRateTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid charge rate type code: " + code);
    }
}