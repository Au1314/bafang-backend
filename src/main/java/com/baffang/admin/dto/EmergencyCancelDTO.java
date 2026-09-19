package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "取消呼叫参数")
public class EmergencyCancelDTO {
    
    @NotBlank(message = "取消原因不能为空")
    @Size(max = 500, message = "取消原因不能超过500字")
    @Schema(description = "取消原因", required = true)
    private String cancelReason;
}
