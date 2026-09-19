package com.baffang.admin.vo;

import lombok.Data;

@Data
public class VehicleAuditStatisticsVO {
    
    /**
     * 待审核数量
     */
    private Integer pending;
    
    /**
     * 审核中数量
     */
    private Integer processing;
    
    /**
     * 已通过数量
     */
    private Integer approved;
    
    /**
     * 已拒绝数量
     */
    private Integer rejected;
    
    /**
     * 需补充材料数量
     */
    private Integer needSupplement;
    
    /**
     * 今日处理数量
     */
    private Integer todayProcessed;
}
