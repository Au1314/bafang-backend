package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "紧急呼叫详情")
public class EmergencyCallDetailVO {
    
    @Schema(description = "呼叫ID")
    private Integer id;
    
    // 呼叫用户信息
    @Schema(description = "呼叫用户ID")
    private Integer requestUserId;
    
    @Schema(description = "呼叫用户昵称")
    private String requestUserName;
    
    @Schema(description = "呼叫用户手机号（脱敏）")
    private String requestUserPhone;
    
    @Schema(description = "呼叫用户头像")
    private String requestUserAvatar;
    
    // 呼叫信息
    @Schema(description = "呼叫类型 0:拖车 1:充电桩 2:其他")
    private Integer type;
    
    @Schema(description = "呼叫类型名称")
    private String typeName;
    
    @Schema(description = "呼叫范围(公里)")
    private Double callRange;
    
    @Schema(description = "呼叫时间")
    private LocalDateTime callTime;
    
    @Schema(description = "呼叫位置")
    private String callPosition;
    
    @Schema(description = "呼叫位置经度")
    private Double callLongitude;
    
    @Schema(description = "呼叫位置纬度")
    private Double callLatitude;
    
    // 目标位置
    @Schema(description = "目标位置")
    private String targetPosition;
    
    @Schema(description = "目标位置经度")
    private Double targetLongitude;
    
    @Schema(description = "目标位置纬度")
    private Double targetLatitude;
    
    // 响应管理员信息
    @Schema(description = "响应管理员ID")
    private Integer responseAdminId;
    
    @Schema(description = "响应管理员昵称")
    private String responseAdminName;
    
    // 移除了 responseAdminPhone，因为 admin_info 表没有手机号字段
    
    // 状态信息
    @Schema(description = "状态 0:待响应 1:已响应 2:已完成 3:已取消")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

