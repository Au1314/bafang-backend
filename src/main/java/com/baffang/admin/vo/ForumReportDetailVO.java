package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "举报详情")
public class ForumReportDetailVO {
    
    // ========== 审核信息 ==========
    @Schema(description = "审核记录ID")
    private Integer id;
    
    @Schema(description = "审核单号")
    private String auditNo;
    
    @Schema(description = "审核类型")
    private Integer auditType;
    
    @Schema(description = "审核类型名称")
    private String auditTypeName;
    
    @Schema(description = "业务ID")
    private Integer businessId;
    
    @Schema(description = "举报标题")
    private String title;
    
    @Schema(description = "举报内容/理由")
    private String content;
    
    @Schema(description = "附件")
    private String attachments;
    
    @Schema(description = "附件列表")
    private List<String> attachmentList;
    
    @Schema(description = "当前状态")
    private Integer currentStatus;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    
    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
    
    @Schema(description = "最终审核意见")
    private String finalOpinion;
    
    @Schema(description = "拒绝原因")
    private String rejectReason;
    
    // ========== 举报人信息 ==========
    @Schema(description = "举报人ID")
    private Integer applicantId;
    
    @Schema(description = "举报人用户名")
    private String applicantUsername;
    
    @Schema(description = "举报人姓名")
    private String applicantName;
    
    @Schema(description = "举报人头像")
    private String applicantAvatar;
    
    @Schema(description = "举报人手机号（脱敏）")
    private String applicantPhone;
    
    // ========== 被举报内容信息 ==========
    @Schema(description = "被举报内容（帖子标题或评论内容）")
    private String reportedContent;
    
    @Schema(description = "被举报人ID")
    private Integer reportedUserId;
    
    @Schema(description = "被举报人姓名")
    private String reportedUserName;
    
    @Schema(description = "被举报人头像")
    private String reportedUserAvatar;
    
    // ========== 处理人信息 ==========
    @Schema(description = "当前处理人ID")
    private Integer currentAdminId;
    
    @Schema(description = "当前处理人")
    private String currentAdminName;
    
    // ========== 审核日志 ==========
    @Schema(description = "审核日志")
    private List<AuditLogVO> auditLogs;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "被举报帖子的图片列表")
private List<String> reportedPostImages;
}
