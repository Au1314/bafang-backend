package com.baffang.admin.service.impl;

import com.baffang.admin.dto.ComplaintAuditDTO;
import com.baffang.admin.dto.ComplaintQueryDTO;
import com.baffang.admin.mapper.ComplaintAuditMapper;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.admin.service.ComplaintAuditService;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.ComplaintDetailVO;
import com.baffang.admin.vo.ComplaintListVO;
import com.baffang.admin.vo.ComplaintStatisticsVO;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.exception.BusinessException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ComplaintAuditServiceImpl implements ComplaintAuditService {

    @Autowired
    private ComplaintAuditMapper complaintAuditMapper;

    @Autowired
    private AdminNoticeService noticeService;  // 新增

    @Override
    public Page<ComplaintListVO> getComplaintList(ComplaintQueryDTO query) {
        ComplaintQueryDTO finalQuery = query;
        if (finalQuery == null) {
            finalQuery = new ComplaintQueryDTO();
        }
        
        int pageNum = finalQuery.getPageNum() == null || finalQuery.getPageNum() < 1 ? 1 : finalQuery.getPageNum();
        int pageSize = finalQuery.getPageSize() == null || finalQuery.getPageSize() < 1 ? 10 : finalQuery.getPageSize();
        
        PageHelper.startPage(pageNum, pageSize);
        Page<ComplaintListVO> page = complaintAuditMapper.selectComplaintList(finalQuery);
        
        page.forEach(this::convertNames);
        
        return page;
    }

    @Override
    public ComplaintDetailVO getComplaintDetail(Integer id) {
        ComplaintDetailVO detail = complaintAuditMapper.selectComplaintDetail(id);
        if (detail == null) {
            throw new BusinessException("数据不存在");
        }
        
        detail.setAuditTypeName(getAuditTypeName(detail.getAuditType()));
        detail.setStatusName(getStatusName(detail.getCurrentStatus()));
        detail.setPriorityName(getPriorityName(detail.getPriority()));
        
        if (StringUtils.hasText(detail.getAttachments())) {
            detail.setImageList(Arrays.asList(detail.getAttachments().split(",")));
        } else {
            detail.setImageList(Collections.emptyList());
        }
        
        List<AuditLogVO> logs = complaintAuditMapper.selectAuditLogs(id);
        logs.forEach(logItem -> {
            logItem.setActionName(getActionName(logItem.getAction()));
            logItem.setFromStatusName(getStatusName(logItem.getFromStatus()));
            logItem.setToStatusName(getStatusName(logItem.getToStatus()));
        });
        detail.setAuditLogs(logs);
        
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComplaint(Integer id, ComplaintAuditDTO dto, Integer adminId) {
        // 1. 查询审核记录
        AuditRecord record = complaintAuditMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("数据不存在");
        }
        
        // 2. 检查是否可审核
        if (record.getCurrentStatus() == 2 || record.getCurrentStatus() == 3) {
            throw new BusinessException("该记录已完成审核，不可重复操作");
        }
        
        // 3. 校验拒绝原因
        if (dto.getAction() == 2 && !StringUtils.hasText(dto.getRejectReason())) {
            throw new BusinessException("拒绝时必须填写拒绝原因");
        }
        
        // 4. 计算目标状态
        Integer fromStatus = record.getCurrentStatus();
        Integer toStatus = calculateToStatus(dto.getAction());
        
        // 5. 更新审核记录
        record.setCurrentStatus(toStatus);
        record.setCurrentAdminId(adminId);
        record.setUpdateTime(new Date());
        
        if (dto.getAction() == 1 || dto.getAction() == 2) {
            record.setFinishTime(new Date());
            record.setFinalOpinion(dto.getOpinion());
        }
        
        if (dto.getAction() == 2) {
            record.setRejectReason(dto.getRejectReason());
        }
        
        if (dto.getPriority() != null) {
            record.setPriority(dto.getPriority());
        }
        
        complaintAuditMapper.updateRecord(record);
        
        // 6. 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(id);
        auditLog.setAdminId(adminId);
        auditLog.setAction(dto.getAction());
        auditLog.setFromStatus(fromStatus);
        auditLog.setToStatus(toStatus);
        auditLog.setOpinion(dto.getOpinion());
        auditLog.setCreateTime(new Date());
        
        complaintAuditMapper.insertAuditLog(auditLog);
        
        log.info("审核完成，id={}, 原状态={}, 新状态={}", id, fromStatus, toStatus);

        // ========== 7. 发送通知给申请人 ==========
        sendComplaintAuditNotice(record, dto, toStatus);
    }

    @Override
    public ComplaintStatisticsVO getStatistics() {
        return complaintAuditMapper.selectStatistics();
    }

    // ==================== 发送通知的私有方法 ====================

    /**
     * 发送投诉/故障审核通知
     */
    private void sendComplaintAuditNotice(AuditRecord record, ComplaintAuditDTO dto, Integer toStatus) {
        if (record.getApplicantId() == null) return;
        
        try {
            String typeName = record.getAuditType() == 2 ? "投诉" : "故障报修";
            String title;
            String content;
            
            switch (toStatus) {
                case 2: // 通过
                    title = typeName + "已处理";
                    content = String.format("您提交的%s已处理完成。%s", 
                            typeName,
                            dto.getOpinion() != null ? "处理意见：" + dto.getOpinion() : "");
                    break;
                case 3: // 拒绝
                    title = typeName + "未通过";
                    content = String.format("您提交的%s未通过审核。原因：%s", 
                            typeName,
                            dto.getRejectReason() != null ? dto.getRejectReason() : "不符合处理条件");
                    break;
                case 4: // 需补充材料
                    title = typeName + "需补充材料";
                    content = String.format("您提交的%s需要补充材料。%s", 
                            typeName,
                            dto.getOpinion() != null ? "说明：" + dto.getOpinion() : "请登录查看详情。");
                    break;
                default:
                    return;
            }
            
            noticeService.sendComplaintNotice(record.getApplicantId(), record.getId(), title, content);
        } catch (Exception e) {
            log.error("发送投诉/故障审核通知失败: auditId={}", record.getId(), e);
        }
    }

    // ===== 私有方法 =====
    
    private void convertNames(ComplaintListVO item) {
        item.setAuditTypeName(getAuditTypeName(item.getAuditType()));
        item.setStatusName(getStatusName(item.getCurrentStatus()));
        item.setPriorityName(getPriorityName(item.getPriority()));
    }
    
    private Integer calculateToStatus(Integer action) {
        switch (action) {
            case 1: return 2; // 通过
            case 2: return 3; // 拒绝
            case 3: return 4; // 要求补充材料
            default: return 1; // 审核中
        }
    }

    private String getAuditTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 2: return "充电桩投诉";
            case 3: return "充电桩故障";
            default: return "未知";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核中";
            case 2: return "已通过";
            case 3: return "已拒绝";
            case 4: return "需补充材料";
            case 5: return "已撤回";
            default: return "未知";
        }
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "";
        switch (priority) {
            case 0: return "低";
            case 1: return "普通";
            case 2: return "高";
            case 3: return "紧急";
            default: return "未知";
        }
    }

    private String getActionName(Integer action) {
        if (action == null) return "";
        switch (action) {
            case 0: return "提交审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            case 3: return "要求补充材料";
            case 4: return "转交他人";
            case 5: return "撤回";
            case 6: return "重新提交";
            default: return "未知";
        }
    }
}
