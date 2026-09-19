package com.baffang.admin.dto;


import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 修改管理员身份请求DTO
 */
@Data
public class AdminTypeUpdateDTO {

    /**
     * 目标管理员ID
     */
    @NotNull(message = "目标管理员ID不能为空")
    private Long targetAdminId;

    /**
     * 新的身份类型
     * 1-普通管理员, 2-审核员, 3-客服, 4-紧急响应人员, 5-论坛管理员, 10-未分配
     */
    @NotNull(message = "身份类型不能为空")
    private Integer newType;
}
