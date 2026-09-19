package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "投诉/故障详情")
public class ComplaintDetailVO {
    
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "审核单号")
    private String auditNo;
    
    @Schema(description = "审核类型")
    private Integer auditType;
    
    @Schema(description = "类型名称")
    private String auditTypeName;
    
    // ===== 充电桩信息 =====
    @Schema(description = "充电桩ID")
    private Integer stationId;
    
    @Schema(description = "充电桩名称")
    private String stationName;
    
    @Schema(description = "充电桩位置")
    private String stationLocation;
    
    @Schema(description = "桩主ID")
    private Integer stationOwnerId;
    
    @Schema(description = "桩主昵称")
    private String stationOwnerName;
    
    @Schema(description = "桩主手机")
    private String stationOwnerPhone;
    
    // ===== 申请人信息 =====
    @Schema(description = "申请人ID")
    private Integer applicantId;
    
    @Schema(description = "申请人昵称")
    private String applicantName;
    
    @Schema(description = "申请人手机")
    private String applicantPhone;
    
    @Schema(description = "申请人头像")
    private String applicantAvatar;
    
    // ===== 内容信息 =====
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "详细内容")
    private String content;
    
    @Schema(description = "附件URL（原始）")
    private String attachments;
    
    @Schema(description = "图片列表")
    private List<String> imageList;
    
    // ===== 状态信息 =====
    @Schema(description = "当前状态")
    private Integer currentStatus;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "当前处理人ID")
    private Integer currentAdminId;
    
    @Schema(description = "当前处理人")
    private String currentAdminName;
    
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
    
    // ===== 审核日志 =====
    @Schema(description = "审核日志列表")
    private List<AuditLogVO> auditLogs;
}
