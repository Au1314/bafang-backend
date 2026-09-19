package com.baffang.common.enums;

import lombok.Getter;

/**
 * 审核操作类型枚举
 */
@Getter
public enum AuditActionEnum {
    
    SUBMIT(0, "提交审核"),
    APPROVE(1, "审核通过"),
    REJECT(2, "审核拒绝"),
    REQUIRE_SUPPLEMENT(3, "要求补充材料"),
    TRANSFER(4, "转交他人"),
    WITHDRAW(5, "撤回"),
    RESUBMIT(6, "重新提交");
    
    private final Integer code;
    private final String desc;
    
    AuditActionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        for (AuditActionEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}
