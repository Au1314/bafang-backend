package com.baffang.admin.dto;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class InviteCodeGenerateDTO {
    
    /**
     * 注册后分配的角色类型
     * 0:超级管理员，1：普通管理员，2：审核员，3：客服，4：紧急响应人员，5：论坛管理员，10：未分配身份
     */
    @Min(value = 0, message = "角色类型无效")
    @Max(value = 10, message = "角色类型无效")
    private Integer targetType = 10;
    
    /**
     * 最大使用次数
     */
    @Min(value = 1, message = "最大使用次数至少为1")
    @Max(value = 100, message = "最大使用次数不能超过100")
    private Integer maxUses = 1;
    
    /**
     * 过期小时数（null表示永不过期）
     */
    private Integer expireHours;
}
