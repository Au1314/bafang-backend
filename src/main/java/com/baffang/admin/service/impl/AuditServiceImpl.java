package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.service.AuditService;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.enums.AuditActionEnum;
import com.baffang.common.enums.AuditStatusEnum;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditRecordMapper auditRecordMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditRecord createAuditRecord(Integer auditType, Integer businessId, Integer applicantId,
                                          String title, String content, String attachments) {
        AuditRecord record = new AuditRecord();
        record.setAuditNo(generateAuditNo(auditType));
        record.setAuditType(auditType);
        record.setBusinessId(businessId);
        record.setApplicantId(applicantId);
        record.setTitle(title);
        record.setContent(content);
        record.setAttachments(attachments);
        record.setCurrentStatus(AuditStatusEnum.PENDING.getCode());
        record.setPriority(1); // 默认普通优先级
        record.setSubmitTime(new Date());
        
        auditRecordMapper.insert(record);
        
        // 记录操作日志（提交审核）
        saveAuditLog(record.getId(), null, AuditActionEnum.SUBMIT.getCode(),
                null, AuditStatusEnum.PENDING.getCode(), "提交审核申请");
        
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Integer auditId, Integer adminId, String opinion) {
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
        }
        
        Integer fromStatus = record.getCurrentStatus();
        
        // 检查状态是否可以审核
        if (fromStatus != AuditStatusEnum.PENDING.getCode() && 
            fromStatus != AuditStatusEnum.PROCESSING.getCode()) {
            throw new BusinessException(ErrorMsg.AUDIT_STATUS_INVALID);
        }
        
        // 更新审核记录（结果/意见）
        auditRecordMapper.finishAudit(auditId, AuditStatusEnum.APPROVED.getCode(), opinion, null);
        // ========== 新增：填充 current_admin_id ==========
        auditRecordMapper.updateStatus(auditId, AuditStatusEnum.APPROVED.getCode(), adminId);
        
        // 记录操作日志
        saveAuditLog(auditId, adminId, AuditActionEnum.APPROVE.getCode(),
                fromStatus, AuditStatusEnum.APPROVED.getCode(), opinion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Integer auditId, Integer adminId, String opinion, String rejectReason) {
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
        }
        
        Integer fromStatus = record.getCurrentStatus();
        
        if (fromStatus != AuditStatusEnum.PENDING.getCode() && 
            fromStatus != AuditStatusEnum.PROCESSING.getCode()) {
            throw new BusinessException(ErrorMsg.AUDIT_STATUS_INVALID);
        }
        
        // 更新审核记录（结果/意见/拒绝原因）
        auditRecordMapper.finishAudit(auditId, AuditStatusEnum.REJECTED.getCode(), opinion, rejectReason);
        // ========== 新增：填充 current_admin_id ==========
        auditRecordMapper.updateStatus(auditId, AuditStatusEnum.REJECTED.getCode(), adminId);
        
        saveAuditLog(auditId, adminId, AuditActionEnum.REJECT.getCode(),
                fromStatus, AuditStatusEnum.REJECTED.getCode(), opinion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requireSupplement(Integer auditId, Integer adminId, String opinion) {
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
        }
        
        Integer fromStatus = record.getCurrentStatus();
        
        auditRecordMapper.updateStatus(auditId, AuditStatusEnum.NEED_SUPPLEMENT.getCode(), adminId);
        
        saveAuditLog(auditId, adminId, AuditActionEnum.REQUIRE_SUPPLEMENT.getCode(),
                fromStatus, AuditStatusEnum.NEED_SUPPLEMENT.getCode(), opinion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmit(Integer auditId, String content, String attachments) {
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
        }
        
        // 只有"需补充材料"或"已拒绝"状态才能重新提交
        if (record.getCurrentStatus() != AuditStatusEnum.NEED_SUPPLEMENT.getCode() &&
            record.getCurrentStatus() != AuditStatusEnum.REJECTED.getCode()) {
            throw new BusinessException(ErrorMsg.AUDIT_STATUS_INVALID);
        }
        
        Integer fromStatus = record.getCurrentStatus();
        
        record.setContent(content);
        record.setAttachments(attachments);
        record.setCurrentStatus(AuditStatusEnum.PENDING.getCode());
        record.setSubmitTime(new Date());
        record.setFinishTime(null);
        record.setFinalOpinion(null);
        record.setRejectReason(null);
        
        auditRecordMapper.updateById(record);
        
        saveAuditLog(auditId, null, AuditActionEnum.RESUBMIT.getCode(),
                fromStatus, AuditStatusEnum.PENDING.getCode(), "重新提交审核");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Integer auditId, Integer applicantId) {
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
        }
        
        // 验证是否为申请人
        if (!record.getApplicantId().equals(applicantId)) {
            throw new BusinessException(ErrorMsg.NO_PERMISSION);
        }
        
        // 只有待审核状态才能撤回
        if (record.getCurrentStatus() != AuditStatusEnum.PENDING.getCode()) {
            throw new BusinessException(ErrorMsg.AUDIT_CANNOT_WITHDRAW);
        }
        
        Integer fromStatus = record.getCurrentStatus();
        
        auditRecordMapper.updateStatus(auditId, AuditStatusEnum.WITHDRAWN.getCode(), null);
        
        saveAuditLog(auditId, null, AuditActionEnum.WITHDRAW.getCode(),
                fromStatus, AuditStatusEnum.WITHDRAWN.getCode(), "用户撤回申请");
    }

    @Override
    public AuditRecord getByBusinessIdAndType(Integer businessId, Integer auditType) {
        return auditRecordMapper.selectByBusinessIdAndType(businessId, auditType);
    }

    @Override
    public List<AuditLog> getAuditLogs(Integer auditId) {
        return auditLogMapper.selectByAuditId(auditId);
    }

    @Override
    public String generateAuditNo(Integer auditType) {
        // 格式：类型前缀 + 日期 + 随机数
        String prefix;
        switch (auditType) {
            case 0: prefix = "STA"; break;  // Station Auth
            case 1: prefix = "CAR"; break;  // Car Auth
            case 2: prefix = "CMP"; break;  // Complaint
            case 3: prefix = "FLT"; break;  // Fault
            case 4: prefix = "RPT"; break;  // Report
            case 5: prefix = "APL"; break;  // Appeal
            default: prefix = "AUD"; break;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        String random = String.format("%04d", new Random().nextInt(10000));
        
        return prefix + dateStr + random;
    }
    
    private void saveAuditLog(Integer auditId, Integer adminId, Integer action,
                              Integer fromStatus, Integer toStatus, String opinion) {
        AuditLog log = new AuditLog();
        log.setAuditId(auditId);
        log.setAdminId(adminId);
        log.setAction(action);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOpinion(opinion);
        
        auditLogMapper.insert(log);
    }
}