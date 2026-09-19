package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "完成呼叫参数")
public class EmergencyCompleteDTO {
    
    @Size(max = 500, message = "备注不能超过500字")
    @Schema(description = "完成备注")
    private String remark;
}
