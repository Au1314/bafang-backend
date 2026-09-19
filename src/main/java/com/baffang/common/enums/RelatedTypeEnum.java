package com.baffang.common.enums;

import lombok.Getter;

/**
 * 关联业务类型枚举
 */
@Getter
public enum RelatedTypeEnum {

    ORDER(0, "订单"),
    RESERVATION(1, "预约"),
    V2V_CALL(2, "V2V呼叫"),
    EMERGENCY_CALL(3, "紧急呼叫"),
    POST(4, "帖子"),
    REVIEW(5, "评价"),
    STATION(6, "充电站"),
    VEHICLE(7, "车辆");  // 新增：车辆认证

    private final Integer code;
    private final String description;

    RelatedTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RelatedTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (RelatedTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static String getDescription(Integer code) {
        RelatedTypeEnum e = fromCode(code);
        return e != null ? e.getDescription() : null;
    }
}
