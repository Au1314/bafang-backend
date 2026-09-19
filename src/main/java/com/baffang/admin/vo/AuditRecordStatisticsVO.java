package com.baffang.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class AuditRecordStatisticsVO {
    
    /**
     * 总数
     */
    private Integer totalCount;
    
    /**
     * 待审核数量
     */
    private Integer pendingCount;
    
    /**
     * 审核中数量
     */
    private Integer processingCount;
    
    /**
     * 已通过数量
     */
    private Integer approvedCount;
    
    /**
     * 已拒绝数量
     */
    private Integer rejectedCount;
    
    /**
     * 今日新增
     */
    private Integer todayCount;
    
    /**
     * 按类型统计
     */
    private List<TypeStatisticsVO> typeStatistics;
    
    @Data
    public static class TypeStatisticsVO {
        private Integer auditType;
        private String auditTypeName;
        private Integer count;
        private Integer pendingCount;
    }
}
