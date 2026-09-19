package com.baffang.common.enums;

import lombok.Getter;

/**
 * 电流类型枚举
 */
@Getter
public enum CurrentTypeEnum {
    DC(0, "直流电"),
    AC(1, "交流电");

    private final Integer code;
    private final String description;

    CurrentTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        for (CurrentTypeEnum e : values()) {
            if (e.code.equals(code)) return e.description;
        }
        return "未知";
    }
}
