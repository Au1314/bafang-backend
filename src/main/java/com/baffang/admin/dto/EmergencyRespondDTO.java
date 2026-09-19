package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "响应紧急呼叫参数")
public class EmergencyRespondDTO {
    
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围为-180到180")
    @DecimalMax(value = "180", message = "经度范围为-180到180")
    @Schema(description = "响应员当前经度", required = true)
    private Double longitude;
    
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围为-90到90")
    @DecimalMax(value = "90", message = "纬度范围为-90到90")
    @Schema(description = "响应员当前纬度", required = true)
    private Double latitude;
}
