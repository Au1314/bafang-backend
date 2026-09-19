package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "AI决策列表VO")
public class AIDecisionListVO {
    
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "决策编号")
    private String decisionNo;
    
    @Schema(description = "决策类型")
    private Integer decisionType;
    
    @Schema(description = "决策类型名称")
    private String decisionTypeName;
    
    @Schema(description = "业务类型")
    private String businessType;
    
    @Schema(description = "业务ID")
    private Integer businessId;
    
    @Schema(description = "置信度")
    private BigDecimal confidence;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "是否自动执行")
    private Integer autoExecute;
    
    @Schema(description = "处理人ID")
    private Integer handledBy;
    
    @Schema(description = "处理人姓名")
    private String handlerName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
}
