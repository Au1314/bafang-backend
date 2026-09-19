package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "审核请求")
public class ComplaintAuditDTO {
    
    @NotNull(message = "操作类型不能为空")
    @Schema(description = "操作类型：1-通过，2-拒绝，3-要求补充材料")
    private Integer action;
    
    @Schema(description = "审核意见")
    private String opinion;
    
    @Schema(description = "拒绝原因（拒绝时必填）")
    private String rejectReason;
    
    @Schema(description = "优先级调整")
    private Integer priority;
}
