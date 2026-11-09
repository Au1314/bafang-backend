package com.baffang.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 通用审核动作请求（audit/record 的动作接口）
 */
@Data
public class AuditCompleteDTO {

    /**
     * 动作：approve-通过，reject-拒绝，supplement-要求补充材料
     */
    @NotBlank(message = "审核动作不能为空")
    private String action;

    /**
     * 审核意见（通过/补充材料时的说明）
     */
    private String opinion;

    /**
     * 拒绝原因（reject 时必填）
     */
    private String rejectReason;
}
