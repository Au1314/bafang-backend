package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "审核日志")
public class AuditLogVO {
    
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "操作人ID")
    private Integer adminId;
    
    @Schema(description = "操作人")
    private String adminName;
    
    @Schema(description = "操作类型")
    private Integer action;
    
    @Schema(description = "操作名称")
    private String actionName;
    
    @Schema(description = "操作前状态")
    private Integer fromStatus;
    
    @Schema(description = "操作后状态")
    private Integer toStatus;
    
    @Schema(description = "操作前状态名称")
    private String fromStatusName;
    
    @Schema(description = "操作后状态名称")
    private String toStatusName;
    
    @Schema(description = "审核意见")
    private String opinion;
    
    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
