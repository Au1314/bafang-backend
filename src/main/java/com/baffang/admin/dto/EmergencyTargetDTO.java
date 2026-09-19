package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新目标位置参数")
public class EmergencyTargetDTO {
    
    @NotBlank(message = "目标位置不能为空")
    @Schema(description = "目标位置描述", required = true)
    private String targetPosition;
    
    @NotNull(message = "目标经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围为-180到180")
    @DecimalMax(value = "180", message = "经度范围为-180到180")
    @Schema(description = "目标位置经度", required = true)
    private Double targetLongitude;
    
    @NotNull(message = "目标纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围为-90到90")
    @DecimalMax(value = "90", message = "纬度范围为-90到90")
    @Schema(description = "目标位置纬度", required = true)
    private Double targetLatitude;
}
