package com.baffang.common.enums;

import lombok.Getter;

/**
 * 审核类型枚举
 */
@Getter
public enum AuditTypeEnum {

    STATION_AUTH(0, "私桩认证"),
    CAR_AUTH(1, "车主认证"),
    STATION_COMPLAINT(2, "充电桩投诉"),
    STATION_FAULT(3, "充电桩故障"),
    COMMENT_REPORT(4, "评论举报"),
    POST_REPORT(5, "帖子举报"),
    TRADE_APPEAL(6, "交易申诉");

    private final Integer code;
    private final String desc;

    AuditTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (AuditTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}
