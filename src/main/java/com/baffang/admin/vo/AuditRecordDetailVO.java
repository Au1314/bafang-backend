package com.baffang.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuditRecordDetailVO {
    
    // ========== 审核信息 ==========
    private Integer id;
    private String auditNo;
    private Integer auditType;
    private String auditTypeName;
    private Integer businessId;
    private String title;
    private String content;
    private String attachments;
    private List<String> attachmentList;
    private Integer currentStatus;
    private String statusName;
    private Integer priority;
    private String priorityName;
    private LocalDateTime submitTime;
    private LocalDateTime finishTime;
    private String finalOpinion;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // ========== 申请人信息 ==========
    private Integer applicantId;
    private String applicantUsername;
    private String applicantName;
    private String applicantAvatar;
    private String applicantPhone;
    
    // ========== 处理人信息 ==========
    private Integer currentAdminId;
    private String currentAdminName;
    
    // ========== 审核日志 ==========
    private List<AuditLogVO> auditLogs;
}
