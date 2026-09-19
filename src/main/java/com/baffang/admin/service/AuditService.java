package com.baffang.admin.service;

import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;

import java.util.List;

/**
 * 通用审核服务
 */
public interface AuditService {
    
    /**
     * 创建审核记录
     */
    AuditRecord createAuditRecord(Integer auditType, Integer businessId, Integer applicantId,
                                   String title, String content, String attachments);
    
    /**
     * 审核通过
     */
    void approve(Integer auditId, Integer adminId, String opinion);
    
    /**
     * 审核拒绝
     */
    void reject(Integer auditId, Integer adminId, String opinion, String rejectReason);
    
    /**
     * 要求补充材料
     */
    void requireSupplement(Integer auditId, Integer adminId, String opinion);
    
    /**
     * 重新提交（用户端调用）
     */
    void resubmit(Integer auditId, String content, String attachments);
    
    /**
     * 撤回（用户端调用）
     */
    void withdraw(Integer auditId, Integer applicantId);
    
    /**
     * 根据业务ID和类型获取审核记录
     */
    AuditRecord getByBusinessIdAndType(Integer businessId, Integer auditType);
    
    /**
     * 获取审核操作日志
     */
    List<AuditLog> getAuditLogs(Integer auditId);
    
    /**
     * 生成审核单号
     */
    String generateAuditNo(Integer auditType);
}
