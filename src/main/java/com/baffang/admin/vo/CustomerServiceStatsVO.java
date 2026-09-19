// CustomerServiceStatsVO.java
package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客服统计VO")
public class CustomerServiceStatsVO {
    
    @Schema(description = "今日接待数")
    private Integer todayCount;
    
    @Schema(description = "今日已完成数")
    private Integer todayCompletedCount;
    
    @Schema(description = "当前进行中对话数")
    private Integer ongoingCount;
    
    @Schema(description = "待接入对话数")
    private Integer pendingCount;
    
    @Schema(description = "今日平均响应时间(秒)")
    private Integer avgResponseTime;
    
    @Schema(description = "今日消息总数")
    private Integer todayMessageCount;
    
    @Schema(description = "本周接待数")
    private Integer weekCount;
    
    @Schema(description = "本月接待数")
    private Integer monthCount;
}
