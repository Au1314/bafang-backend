package com.baffang.admin.dto;

import lombok.Data;

@Data
public class AuditRecordQueryDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 审核类型：0-私桩认证，1-车主认证，2-充电桩投诉，3-充电桩故障，4-评论举报，5-交易申诉
     */
    private Integer auditType;
    
    /**
     * 审核状态：0-待审核，1-审核中，2-已通过，3-已拒绝，4-需补充材料，5-已撤回
     */
    private Integer status;
    
    /**
     * 优先级：0-低，1-普通，2-高，3-紧急
     */
    private Integer priority;
    
    /**
     * 关键字（审核单号、标题）
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

    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
    }
}
