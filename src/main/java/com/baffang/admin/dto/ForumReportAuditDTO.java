package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "举报审核参数")
public class ForumReportAuditDTO {
    
    @Size(max = 500, message = "审核意见不能超过500字")
    @Schema(description = "审核意见")
    private String opinion;
    
    @Size(max = 255, message = "拒绝原因不能超过255字")
    @Schema(description = "拒绝原因")
    private String rejectReason;
    
    @Schema(description = "是否删除被举报内容", defaultValue = "false")
    private Boolean deleteContent = false;
}
