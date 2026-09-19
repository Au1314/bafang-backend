package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "紧急呼叫列表")
public class EmergencyCallListVO {
    
    @Schema(description = "呼叫ID")
    private Integer id;
    
    @Schema(description = "呼叫用户ID")
    private Integer requestUserId;
    
    @Schema(description = "呼叫用户姓名")
    private String requestUserName;
    
    @Schema(description = "呼叫用户手机号（脱敏）")
    private String requestUserPhone;
    
    @Schema(description = "呼叫类型：0-拖车，1-充电桩，2-其他")
    private Integer type;
    
    @Schema(description = "呼叫类型名称")
    private String typeName;
    
    @Schema(description = "响应管理员ID")
    private Integer responseAdminId;
    
    @Schema(description = "响应管理员姓名")
    private String responseAdminName;
    
    @Schema(description = "呼叫范围（公里）")
    private Double callRange;
    
    @Schema(description = "呼叫时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime callTime;
    
    @Schema(description = "呼叫位置")
    private String callPosition;
    
    @Schema(description = "呼叫位置经度")
    private Double callLongitude;
    
    @Schema(description = "呼叫位置纬度")
    private Double callLatitude;
    
    @Schema(description = "目标位置")
    private String targetPosition;
    
    @Schema(description = "状态：0-待响应，1-已响应，2-已完成，3-已取消")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "直线距离（公里）")
    private Double distance;
    
    @Schema(description = "驾车导航距离（公里）- 仅distanceType=1时返回")
    private Double drivingDistance;
    
    @Schema(description = "预计驾车时间（分钟）- 仅distanceType=1时返回")
    private Integer drivingDuration;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
