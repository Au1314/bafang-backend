package com.baffang.user.vo;

import lombok.Data;

/**
 * 充电桩故障/举报提交响应结果VO
 */
@Data
public class ChargingReportVO {

    /**
     * 举报ID
     */
    private Integer reportId;

    /**
     * 举报单号
     */
    private String reportNo;

    /**
     * 状态：0=待处理
     */
    private Integer status;
}