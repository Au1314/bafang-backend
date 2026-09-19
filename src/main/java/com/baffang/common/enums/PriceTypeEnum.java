package com.baffang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 私桩定价类型枚举
 */
@Getter
@AllArgsConstructor
public enum PriceTypeEnum {
    
    FIXED(0, "固定价格"),
    TIME_BASED(1, "分时定价");
    
    private final Integer code;
    private final String desc;
    
    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        for (PriceTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}
