package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员个人信息")
public class AdminProfileVO {
    
    @Schema(description = "管理员ID")
    private Integer id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String name;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "管理员类型：0超级管理员,1普通管理员,2审核员,3客服,4紧急响应人员,5论坛管理员,10未分配身份")
    private Integer type;
    
    @Schema(description = "管理员类型名称")
    private String typeName;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
