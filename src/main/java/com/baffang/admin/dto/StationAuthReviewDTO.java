package com.baffang.admin.dto;

import lombok.Data;

@Data
public class StationAuthReviewDTO {
    
    /**
     * 私桩ID
     */
    private Integer stationId;
    
    /**
     * 审核结果：2-通过，3-拒绝，4-需补充材料
     */
    private Integer authStatus;
    
    /**
     * 审核意见（最终意见）
     */
    private String opinion;
    
    /**
     * 拒绝原因（拒绝时必填）
     */
    private String rejectReason;
}