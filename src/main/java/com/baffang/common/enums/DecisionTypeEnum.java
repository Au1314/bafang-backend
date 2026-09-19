package com.baffang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DecisionTypeEnum {
    
    AUDIT_SUGGESTION(0, "审核建议"),
    ANOMALY_WARNING(1, "异常预警"),
    RESOURCE_SCHEDULE(2, "资源调度"),
    RISK_CONTROL(3, "风险控制"),
    OPERATION_OPTIMIZE(4, "运营优化");
    
    private final int code;
    private final String name;
    
    public static DecisionTypeEnum fromCode(int code) {
        for (DecisionTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
    
    public static String getDesc(Integer code) {
        if (code == null) return "未知";
        DecisionTypeEnum type = fromCode(code);
        return type != null ? type.name : "未知";
    }
}
