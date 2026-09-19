package com.baffang.user.dto;

import lombok.Data;

/**
 * 充电桩故障/举报提交请求参数DTO
 */
@Data
public class ChargingReportDTO {

    /**
     * 充电桩ID
     */
    private Integer stationId;

    /**
     * 上报类型：0=举报/投诉，1=故障
     */
    private Integer reportType;

    /**
     * 标题
     */
    private String title;

    /**
     * 详细描述
     */
    private String content;

    /**
     * 图片URL（多个逗号分隔）
     */
    private String images;
}