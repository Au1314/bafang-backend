package com.baffang.common.enums.entityEnum;

/**
 * 电流类型枚举
 */
public enum CurrentTypeEnum {
    /**
     * 直流电
     */
    DC(0, "直流电"),

    /**
     * 交流电
     */
    AC(1, "交流电");

    private final int code;
    private final String description;

    CurrentTypeEnum(int code, String description) {
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
    public static CurrentTypeEnum fromCode(int code) {
        for (CurrentTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid current type code: " + code);
    }
}