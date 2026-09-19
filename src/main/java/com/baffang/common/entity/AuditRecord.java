package com.baffang.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 审核记录主表
 */
@Data
public class AuditRecord {

    private Integer id;

    /**
     * 审核单号
     */
    private String auditNo;

    /**
     * 审核类型：0-私桩认证，1-车主认证，2-充电桩投诉，3-充电桩故障，4-评论举报，5-交易申诉
     */
    private Integer auditType;

    /**
     * 业务ID（关联对应业务表的主键）
     */
    private Integer businessId;

    /**
     * 申请人/提交人ID
     */
    private Integer applicantId;

    /**
     * 审核标题/摘要
     */
    private String title;

    /**
     * 审核内容/详情描述
     */
    private String content;

    /**
     * 附件URL，多个用逗号分隔
     */
    private String attachments;

    /**
     * 当前状态：0-待审核，1-审核中，2-已通过，3-已拒绝，4-需补充材料，5-已撤回
     */
    private Integer currentStatus;

    /**
     * 当前处理人ID
     */
    private Integer currentAdminId;

    /**
     * 优先级：0-低，1-普通，2-高，3-紧急（使用AuditPriorityEnum）
     */
    private Integer priority;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    /**
     * 最终审核意见
     */
    private String finalOpinion;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    private Integer isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
