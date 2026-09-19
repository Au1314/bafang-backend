package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 私桩认证进度VO
 */
@Data
public class PrivateStationAuthProgressVO {
    /**
     * 私桩ID
     */
    private Integer stationId;
    
    /**
     * 私桩认证状态
     */
    private Integer authStatus;
    
    /**
     * 审核记录ID
     */
    private Integer id;
    
    /**
     * 审核单号
     */
    private String auditNo;
    
    /**
     * 审核类型
     */
    private Integer auditType;
    
    /**
     * 业务ID
     */
    private Integer businessId;
    
    /**
     * 申请人ID
     */
    private Integer applicantId;
    
    /**
     * 审核标题
     */
    private String title;
    
    /**
     * 审核内容
     */
    private String content;
    
    /**
     * 附件URL
     */
    private String attachments;
    
    /**
     * 当前状态
     */
    private Integer currentStatus;
    
    /**
     * 当前处理人ID
     */
    private Integer currentAdminId;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 提交时间
     */
    private Date submitTime;
    
    /**
     * 完成时间
     */
    private Date finishTime;
    
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
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}