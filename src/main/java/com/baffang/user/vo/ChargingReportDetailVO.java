package com.baffang.user.vo;

import lombok.Data;

/**
 * 充电桩故障/举报详细信息VO
 */
@Data
public class ChargingReportDetailVO {

    /**
     * 上报记录ID
     */
    private Integer reportId;

    /**
     * 上报单号
     */
    private String reportNo;

    /**
     * 上报类型：0=举报/投诉，1=故障
     */
    private Integer reportType;

    /**
     * 充电桩ID
     */
    private Integer stationId;

    /**
     * 申请人ID
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 附件URL，多个用逗号分隔
     */
    private String attachments;

    /**
     * 当前状态：0-待审核，1-审核中，2-已通过，3-已拒绝，4-需补充材料，5-已撤回
     */
    private Integer status;

    /**
     * 当前处理人ID
     */
    private Integer adminId;

    /**
     * 优先级：0-低，1-普通，2-高，3-紧急
     */
    private Integer priority;

    /**
     * 提交时间
     */
    private String submitTime;

    /**
     * 完成时间
     */
    private String finishTime;

    /**
     * 最终审核意见
     */
    private String finalOpinion;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}