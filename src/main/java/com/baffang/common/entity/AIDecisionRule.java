package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_decision_rule")
public class AIDecisionRule {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String ruleCode;
    
    private String ruleName;
    
    private Integer decisionType;
    
    private String triggerCondition;
    
    private Integer autoExecute;
    
    private BigDecimal executeThreshold;
    
    private Integer enabled;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
