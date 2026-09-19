package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_decision_action_log")
public class AIDecisionActionLog {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer decisionId;
    
    private String actionType;
    
    private String actionName;
    
    private String actionParams;
    
    private Integer executeStatus;
    
    private String executeResult;
    
    private String errorMsg;
    
    private LocalDateTime executeTime;
    
    private Integer costMs;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
