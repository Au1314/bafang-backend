package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "投诉/故障统计")
public class ComplaintStatisticsVO {
    
    // ===== 投诉统计 =====
    @Schema(description = "投诉总数")
    private Integer complaintTotal;
    
    @Schema(description = "投诉待审核")
    private Integer complaintPending;
    
    @Schema(description = "投诉审核中")
    private Integer complaintProcessing;
    
    @Schema(description = "投诉已通过")
    private Integer complaintPassed;
    
    @Schema(description = "投诉已拒绝")
    private Integer complaintRejected;
    
    // ===== 故障统计 =====
    @Schema(description = "故障总数")
    private Integer faultTotal;
    
    @Schema(description = "故障待审核")
    private Integer faultPending;
    
    @Schema(description = "故障审核中")
    private Integer faultProcessing;
    
    @Schema(description = "故障已通过")
    private Integer faultPassed;
    
    @Schema(description = "故障已拒绝")
    private Integer faultRejected;
    
    // ===== 紧急统计 =====
    @Schema(description = "紧急待处理数")
    private Integer urgentCount;
    
    @Schema(description = "今日新增")
    private Integer todayCount;
}
