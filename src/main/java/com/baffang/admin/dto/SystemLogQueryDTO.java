package com.baffang.admin.dto;

import lombok.Data;

@Data
public class SystemLogQueryDTO {
    
    /**
     * 模块名称
     */
    private String module;
    
    /**
     * 操作名称
     */
    private String action;
    
    /**
     * 管理员ID
     */
    private Integer adminId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 状态：1成功，0失败
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer size = 20;
}
