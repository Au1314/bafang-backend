// AdminStatsVO.java - 单个客服的统计
package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客服人员统计VO")
public class AdminStatsVO {
    
    @Schema(description = "客服ID")
    private Integer adminId;
    
    @Schema(description = "客服名称")
    private String adminName;
    
    @Schema(description = "今日接待数")
    private Integer todayCount;
    
    @Schema(description = "今日完成数")
    private Integer todayCompletedCount;
    
    @Schema(description = "当前进行中")
    private Integer ongoingCount;
    
    @Schema(description = "今日消息数")
    private Integer messageCount;
    
    @Schema(description = "平均响应时间(秒)")
    private Integer avgResponseTime;
}
