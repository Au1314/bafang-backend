package com.baffang.common.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NoticeTypeEnum {

    SYSTEM_ANNOUNCEMENT(0, "系统公告"),
    CHARGING(1, "充电通知"),
    RESERVATION(2, "预约通知"),
    V2V(3, "V2V通知"),
    EMERGENCY_CALL(4, "紧急呼叫通知"),
    ORDER(5, "订单通知"),
    AUDIT(6, "审核通知");

    private final Integer code;
    private final String description;

    NoticeTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static NoticeTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (NoticeTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static String getDescription(Integer code) {
        NoticeTypeEnum e = fromCode(code);
        return e != null ? e.getDescription() : "未知";
    }
}
