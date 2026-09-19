package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "修改密码请求")
public class AdminPasswordUpdateDTO {
    
    @Schema(description = "原密码", required = true)
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @Schema(description = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
    
    @Schema(description = "确认新密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
