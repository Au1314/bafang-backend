package com.baffang.common.enums;

import lombok.Getter;

/**
 * 审核优先级枚举
 */
@Getter
public enum AuditPriorityEnum {
    
    LOW(0, "低"),
    NORMAL(1, "普通"),
    HIGH(2, "高"),
    URGENT(3, "紧急");
    
    private final Integer code;
    private final String desc;
    
    AuditPriorityEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        for (AuditPriorityEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}