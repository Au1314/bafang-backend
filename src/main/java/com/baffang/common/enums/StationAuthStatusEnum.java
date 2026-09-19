package com.baffang.common.enums;

import lombok.Getter;

/**
 * 私桩认证状态枚举
 */
@Getter
public enum StationAuthStatusEnum {
    UNAUTH(0, "未认证"),
    PENDING(1, "审核中"),
    APPROVED(2, "已认证"),
    REJECTED(3, "认证失败");

    private final Integer code;
    private final String description;

    StationAuthStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        for (StationAuthStatusEnum e : values()) {
            if (e.code.equals(code)) return e.description;
        }
        return "未知";
    }
}
