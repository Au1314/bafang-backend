package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "举报列表")
public class ForumReportListVO {
    
    @Schema(description = "审核记录ID")
    private Integer id;
    
    @Schema(description = "审核单号")
    private String auditNo;
    
    @Schema(description = "审核类型：5-帖子举报，4-评论举报")
    private Integer auditType;
    
    @Schema(description = "审核类型名称")
    private String auditTypeName;
    
    @Schema(description = "业务ID（帖子ID或评论ID）")
    private Integer businessId;
    
    @Schema(description = "举报标题")
    private String title;
    
    @Schema(description = "举报内容（截取）")
    private String content;
    
    @Schema(description = "被举报内容（截取）")
    private String reportedContent;
    
    @Schema(description = "当前状态")
    private Integer currentStatus;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "举报人ID")
    private Integer applicantId;
    
    @Schema(description = "举报人姓名")
    private String applicantName;
    
    @Schema(description = "举报人头像")
    private String applicantAvatar;
    
    @Schema(description = "被举报人ID")
    private Integer reportedUserId;
    
    @Schema(description = "被举报人姓名")
    private String reportedUserName;
    
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    
    @Schema(description = "当前处理人")
    private String currentAdminName;
}
