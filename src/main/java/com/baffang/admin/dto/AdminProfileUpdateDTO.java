package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Size;

@Data
@Schema(description = "修改个人信息请求")
public class AdminProfileUpdateDTO {
    
    @Schema(description = "昵称")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String name;
    
    @Schema(description = "头像URL（通过上传接口获取）")
    private String avatar;
}
