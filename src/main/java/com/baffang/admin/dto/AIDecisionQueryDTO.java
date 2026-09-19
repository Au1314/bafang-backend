package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AI决策查询参数")
public class AIDecisionQueryDTO {
    
    @Schema(description = "决策类型")
    private Integer decisionType;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "业务类型")
    private String businessType;
    
    @Schema(description = "开始时间")
    private String startTime;
    
    @Schema(description = "结束时间")
    private String endTime;
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}
