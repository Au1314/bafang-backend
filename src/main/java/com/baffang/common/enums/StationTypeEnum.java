package com.baffang.common.enums;

import lombok.Getter;

/**
 * 私桩类型枚举
 */
@Getter
public enum StationTypeEnum {
    NORMAL(0, "普通私桩"),
    PROFESSIONAL(1, "专业私桩");

    private final Integer code;
    private final String description;

    StationTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        for (StationTypeEnum e : values()) {
            if (e.code.equals(code)) return e.description;
        }
        return "未知";
    }
}
