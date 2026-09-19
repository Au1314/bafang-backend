package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 邀请码表
 */
@Data
public class AdminInviteCode {
    /**
     * 主键（自增）
     */
    private Integer id;
    
    /**
     * 邀请码
     */
    private String code;
    
    /**
     * 创建人 ID（超级管理员）
     */
    private Integer createdBy;
    
    /**
     * 注册后的管理员类型（使用AdminRoleEnum）：0:超级管理员，1：普通管理员，2：审核员，3：客服，4：紧急响应人员，5：论坛管理员，10：未分配身份
     */
    private Integer targetType;
    
    /**
     * 最大使用次数
     */
    private Integer maxUses;
    
    /**
     * 已使用次数
     */
    private Integer usedCount;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * 状态（使用InviteCodeStatusEnum）：0 - 有效，1 - 已禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
