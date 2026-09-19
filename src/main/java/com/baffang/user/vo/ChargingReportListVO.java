package com.baffang.user.vo;

import lombok.Data;

/**
 * 充电桩故障/举报记录列表VO
 */
@Data
public class ChargingReportListVO {

    /**
     * 上报记录ID
     */
    private Integer reportId;

    /**
     * 上报单号
     */
    private String reportNo;

    /**
     * 充电桩ID
     */
    private Integer stationId;

    /**
     * 充电桩名称
     */
    private String stationName;

    /**
     * 上报类型：0=举报，1=故障
     */
    private Integer reportType;

    /**
     * 标题
     */
    private String title;

    /**
     * 处理状态：0=待处理，1=处理中，2=已处理，3=已关闭
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;
}