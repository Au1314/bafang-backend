package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_decision_record")
public class AIDecisionRecord {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String decisionNo;
    
    private Integer decisionType;
    
    private Integer triggerType;
    
    private String businessType;
    
    private Integer businessId;
    
    private String inputData;
    
    private String aiAnalysis;
    
    private String decisionResult;
    
    private String suggestedActions;
    
    private BigDecimal confidence;
    
    private Integer priority;
    
    private Integer status;
    
    private Integer autoExecute;
    
    private String executeResult;
    
    private LocalDateTime executeTime;
    
    private Integer handledBy;
    
    private LocalDateTime handleTime;
    
    private String handleRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
