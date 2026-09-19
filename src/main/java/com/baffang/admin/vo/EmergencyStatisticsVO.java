package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "紧急响应统计")
public class EmergencyStatisticsVO {
    
    @Schema(description = "呼叫总数")
    private Integer totalCallCount;
    
    @Schema(description = "今日呼叫数")
    private Integer todayCallCount;
    
    @Schema(description = "待响应数")
    private Integer pendingCount;
    
    @Schema(description = "响应中数量")
    private Integer respondingCount;
    
    @Schema(description = "已完成数")
    private Integer completedCount;
    
    @Schema(description = "已取消数")
    private Integer cancelledCount;
    
    @Schema(description = "我今日响应数")
    private Integer myTodayResponseCount;
    
    @Schema(description = "我总响应数")
    private Integer myTotalResponseCount;
    
    @Schema(description = "平均响应时间（分钟）")
    private Double avgResponseTime;
}
