package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "AI决策统计VO")
public class AIDecisionStatisticsVO {
    
    @Schema(description = "总决策数")
    private Integer totalCount;
    
    @Schema(description = "待处理数")
    private Integer pendingCount;
    
    @Schema(description = "已采纳数")
    private Integer adoptedCount;
    
    @Schema(description = "已忽略数")
    private Integer ignoredCount;
    
    @Schema(description = "执行成功数")
    private Integer executedCount;
    
    @Schema(description = "执行失败数")
    private Integer failedCount;
    
    @Schema(description = "今日决策数")
    private Integer todayCount;
    
    @Schema(description = "平均置信度")
    private Double avgConfidence;
    
    @Schema(description = "按类型统计")
    private List<Map<String, Object>> byType;
    
    @Schema(description = "按优先级统计")
    private List<Map<String, Object>> byPriority;
}
