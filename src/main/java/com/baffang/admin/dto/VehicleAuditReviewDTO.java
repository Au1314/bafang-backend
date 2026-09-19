package com.baffang.admin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class VehicleAuditReviewDTO {
    
    /**
     * 审核记录ID
     */
    @NotNull(message = "审核ID不能为空")
    private Integer auditId;
    
    /**
     * 操作：1通过、2拒绝、3要求补充材料
     */
    @NotNull(message = "审核操作不能为空")
    private Integer action;
    
    /**
     * 审核意见
     */
    private String opinion;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
}
