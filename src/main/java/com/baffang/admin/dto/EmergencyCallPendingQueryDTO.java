package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "待响应呼叫查询参数")
public class EmergencyCallPendingQueryDTO {
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围为-180到180")
    @DecimalMax(value = "180", message = "经度范围为-180到180")
    @Schema(description = "响应员当前经度", required = true, example = "116.4716")
    private Double longitude;
    
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围为-90到90")
    @DecimalMax(value = "90", message = "纬度范围为-90到90")
    @Schema(description = "响应员当前纬度", required = true, example = "39.9093")
    private Double latitude;
    
    @Schema(description = "呼叫类型：0-拖车，1-充电桩，2-其他")
    private Integer type;
    
    @Schema(description = "最大搜索距离（公里）", example = "50")
    private Double maxDistance = 50.0;
    
    @Schema(description = "距离计算方式：0-直线距离（默认，快速），1-驾车导航距离（精确，较慢）", example = "0")
    private Integer distanceType = 0;

    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
    }
}
