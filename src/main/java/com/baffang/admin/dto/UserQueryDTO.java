package com.baffang.admin.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    /**
     * 关键字（用户名/昵称/手机号）
     */
    private String keyword;
    
    /**
     * 用户类型：0-普通用户，1-VIP用户
     */
    private Integer type;
    
    /**
     * 充电桩认证状态：0-未认证，1-审核中，2-已认证，3-认证失败
     */
    private Integer authChargingStation;
    
    /**
     * 车辆认证状态：0-未认证，1-审核中，2-已认证，3-认证失败
     */
    private Integer authVehicle;
    
    /**
     * 注册开始时间
     */
    private String startTime;
    
    /**
     * 注册结束时间
     */
    private String endTime;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
    }
}
