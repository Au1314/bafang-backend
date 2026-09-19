package com.baffang.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditRecordListVO {
    
    private Integer id;
    private String auditNo;
    private Integer auditType;
    private String auditTypeName;
    private Integer businessId;
    private String title;
    private String content;
    private Integer currentStatus;
    private String statusName;
    private Integer priority;
    private String priorityName;
    private Integer applicantId;
    private String applicantName;
    private String applicantAvatar;
    private LocalDateTime submitTime;
    private LocalDateTime finishTime;
    private Integer currentAdminId;
    private String currentAdminName;
}
