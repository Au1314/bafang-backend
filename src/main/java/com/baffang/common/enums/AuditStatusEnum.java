package com.baffang.common.enums;

import lombok.Getter;

/**
 * 审核状态枚举
 */
@Getter
public enum AuditStatusEnum {
    
    PENDING(0, "待审核"),
    PROCESSING(1, "审核中"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已拒绝"),
    NEED_SUPPLEMENT(4, "需补充材料"),
    WITHDRAWN(5, "已撤回");
    
    private final Integer code;
    private final String desc;
    
    AuditStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        for (AuditStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}
