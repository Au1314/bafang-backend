package com.baffang.admin.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UserStatusDTO {
    /**
     * 状态：0-正常，1-禁用（逻辑删除）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    /**
     * 操作原因
     */
    private String reason;
}
