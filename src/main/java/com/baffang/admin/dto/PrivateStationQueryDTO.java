package com.baffang.admin.dto;

import lombok.Data;

@Data
public class PrivateStationQueryDTO {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    /**
     * 私桩名称（模糊查询）
     */
    private String name;
    
    /**
     * 状态：0-空闲，1-已预约，2-充电中，3-异常
     */
    private Integer status;
    
    /**
     * 认证状态：0-未认证，1-审核中，2-已认证，3-认证失败
     */
    private Integer authStatus;
    
    /**
     * 类型：0-普通私桩，1-专业私桩
     */
    private Integer type;
    
    /**
     * 开放状态：0-关闭，1-开启
     */
    private Integer openStatus;
    
    /**
     * 所有者ID
     */
    private Integer ownerId;


    // ========== 新增字段 ==========
    /**
     * 定价类型：0-固定价格，1-分时定价
     */
    private Integer priceType;
}
