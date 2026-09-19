package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "投诉/故障列表")
public class ComplaintListVO {
    
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "审核单号")
    private String auditNo;
    
    @Schema(description = "审核类型：2-投诉，3-故障")
    private Integer auditType;
    
    @Schema(description = "类型名称")
    private String auditTypeName;
    
    @Schema(description = "充电桩ID")
    private Integer stationId;
    
    @Schema(description = "充电桩名称")
    private String stationName;
    
    @Schema(description = "充电桩位置")
    private String stationLocation;
    
    @Schema(description = "申请人ID")
    private Integer applicantId;
    
    @Schema(description = "申请人昵称")
    private String applicantName;
    
    @Schema(description = "申请人手机")
    private String applicantPhone;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "状态：0-待审核，1-审核中，2-已通过，3-已拒绝，4-需补充材料")
    private Integer currentStatus;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "优先级：0-低，1-普通，2-高，3-紧急")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "当前处理人")
    private String currentAdminName;
    
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
}
