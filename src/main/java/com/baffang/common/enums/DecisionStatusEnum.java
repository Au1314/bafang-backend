package com.baffang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DecisionStatusEnum {
    
    PENDING(0, "待处理"),
    ADOPTED(1, "已采纳"),
    IGNORED(2, "已忽略"),
    PARTIAL_ADOPTED(3, "部分采纳"),
    EXECUTING(4, "自动执行中"),
    EXECUTED(5, "执行完成"),
    EXECUTE_FAILED(6, "执行失败");
    
    private final int code;
    private final String name;
    
    public static DecisionStatusEnum fromCode(int code) {
        for (DecisionStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        DecisionStatusEnum status = fromCode(code);
        return status != null ? status.name : "未知";
    }
}
