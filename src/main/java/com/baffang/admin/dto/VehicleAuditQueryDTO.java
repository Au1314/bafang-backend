package com.baffang.admin.dto;

import lombok.Data;

@Data
public class VehicleAuditQueryDTO {
    
    /**
     * 审核状态：0待审核、1审核中、2已通过、3已拒绝、4需补充材料
     */
    private Integer status;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 关键词（车牌号/用户名/手机号）
     */
    private String keyword;
    
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
    private Integer page ;
    
    /**
     * 每页条数
     */
    private Integer size ;


    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (page == null || page < 1) ? 1 : page;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (size == null || size < 1 || size > 100) ? 20 : size;
    }
}
