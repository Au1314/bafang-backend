package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "论坛统计数据")
public class ForumStatisticsVO {
    
    // ========== 帖子统计 ==========
    @Schema(description = "帖子总数")
    private Integer totalPostCount;
    
    @Schema(description = "今日新增帖子")
    private Integer todayPostCount;
    
    @Schema(description = "置顶帖子数")
    private Integer topPostCount;
    
    @Schema(description = "精华帖子数")
    private Integer essencePostCount;
    
    // ========== 评论统计 ==========
    @Schema(description = "评论总数")
    private Integer totalReplyCount;
    
    @Schema(description = "今日新增评论")
    private Integer todayReplyCount;
    
    // ========== 举报统计 ==========
    @Schema(description = "待处理帖子举报数")
    private Integer pendingPostReportCount;
    
    @Schema(description = "待处理评论举报数")
    private Integer pendingReplyReportCount;
    
    @Schema(description = "本月处理举报数")
    private Integer monthProcessedReportCount;
}
