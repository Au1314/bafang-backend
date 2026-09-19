package com.baffang.common.enums;

import lombok.Getter;

/**
 * 私桩状态枚举
 */
@Getter
public enum StationStatusEnum {
    IDLE(0, "空闲"),
    RESERVED(1, "已预约"),
    CHARGING(2, "充电中"),
    ABNORMAL(3, "异常");

    private final Integer code;
    private final String description;

    StationStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        for (StationStatusEnum e : values()) {
            if (e.code.equals(code)) return e.description;
        }
        return "未知";
    }
}
