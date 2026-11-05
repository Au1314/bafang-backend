package com.baffang.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加紧急呼叫处理日志请求
 */
@Data
public class EmergencyLogCreateDTO {

    /**
     * 日志内容
     */
    @NotBlank(message = "日志内容不能为空")
    @Size(max = 500, message = "日志内容不能超过500字")
    private String content;
}
