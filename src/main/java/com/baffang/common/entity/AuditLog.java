package com.baffang.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 审核操作日志表
 */
@Data
public class AuditLog {

    private Integer id;

    /**
     * 审核记录ID
     */
    private Integer auditId;

    /**
     * 操作人ID
     */
    private Integer adminId;

    /**
     * 操作类型：0-提交审核，1-审核通过，2-审核拒绝，3-要求补充材料，4-转交他人，5-撤回，6-重新提交（使用AuditActionEnum）
     */
    private Integer action;

    /**
     * 操作前状态
     */
    private Integer fromStatus;

    /**
     * 操作后状态
     */
    private Integer toStatus;

    /**
     * 审核意见/操作备注
     */
    private String opinion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
