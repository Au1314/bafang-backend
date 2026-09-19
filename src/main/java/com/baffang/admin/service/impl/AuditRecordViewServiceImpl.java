package com.baffang.admin.service.impl;

import com.baffang.admin.dto.AuditRecordQueryDTO;
import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.service.AuditRecordViewService;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.AuditRecordDetailVO;
import com.baffang.admin.vo.AuditRecordListVO;
import com.baffang.admin.vo.AuditRecordStatisticsVO;
import com.baffang.common.exception.BusinessException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditRecordViewServiceImpl implements AuditRecordViewService {

    private final AuditRecordMapper auditRecordMapper;
    private final AuditLogMapper auditLogMapper;

    // 审核类型名称映射
    private static final Map<Integer, String> AUDIT_TYPE_MAP = new LinkedHashMap<>();
    static {
        AUDIT_TYPE_MAP.put(0, "私桩认证");
        AUDIT_TYPE_MAP.put(1, "车主认证");
        AUDIT_TYPE_MAP.put(2, "充电桩投诉");
        AUDIT_TYPE_MAP.put(3, "充电桩故障");
        AUDIT_TYPE_MAP.put(4, "评论举报");
        AUDIT_TYPE_MAP.put(5, "交易申诉");
    }

    @Override
public Page<AuditRecordListVO> getList(AuditRecordQueryDTO queryDTO) {
    int pageNum = queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
    int pageSize = queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();

    PageHelper.startPage(pageNum, pageSize);
    List<Map<String, Object>> mapList = auditRecordMapper.selectListWithApplicant(queryDTO);
    
    // ========== 核心修复：创建Page时指定泛型<AuditRecordListVO> ==========
    Page<AuditRecordListVO> page = new Page<>();
    // 无需强转，直接设置分页参数
    page.setTotal(((Page<?>) mapList).getTotal());
    page.setPages(((Page<?>) mapList).getPages());
    page.setPageNum(pageNum);
    page.setPageSize(pageSize);

    List<AuditRecordListVO> voList = new ArrayList<>();
    for (Map<String, Object> map : mapList) {
        AuditRecordListVO vo = convertToListVO(map);
        voList.add(vo);
    }
    page.addAll(voList);

    return page;
}

    @Override
    public AuditRecordDetailVO getDetail(Integer id) {
        Map<String, Object> map = auditRecordMapper.selectDetailById(id);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("审核记录不存在");
        }

        AuditRecordDetailVO vo = convertToDetailVO(map);
        
        // 获取审核日志
        vo.setAuditLogs(getAuditLogs(id));

        return vo;
    }

    @Override
    public List<AuditLogVO> getAuditLogs(Integer id) {
        List<Map<String, Object>> logMaps = auditLogMapper.selectLogsWithAdmin(id);
        List<AuditLogVO> logs = new ArrayList<>();
        
        for (Map<String, Object> map : logMaps) {
            AuditLogVO logVO = new AuditLogVO();
            logVO.setId(getInteger(map, "id"));
            logVO.setAction(getInteger(map, "action"));
            logVO.setFromStatus(getInteger(map, "fromStatus"));
            logVO.setToStatus(getInteger(map, "toStatus"));
            logVO.setOpinion(getString(map, "opinion"));
            logVO.setAdminId(getInteger(map, "adminId"));
            logVO.setAdminName(getString(map, "adminName"));
            logVO.setCreateTime(getLocalDateTime(map, "createTime"));
            
            // 转换名称
            logVO.setActionName(convertActionName(logVO.getAction()));
            logVO.setFromStatusName(convertStatusName(logVO.getFromStatus()));
            logVO.setToStatusName(convertStatusName(logVO.getToStatus()));
            
            logs.add(logVO);
        }
        
        return logs;
    }

    @Override
    public AuditRecordStatisticsVO getStatistics() {
        AuditRecordStatisticsVO vo = new AuditRecordStatisticsVO();

        // 总体统计
        Map<String, Object> overallMap = auditRecordMapper.selectOverallStatistics();
        if (overallMap != null) {
            vo.setTotalCount(getIntegerDefault(overallMap, "totalCount", 0));
            vo.setPendingCount(getIntegerDefault(overallMap, "pendingCount", 0));
            vo.setProcessingCount(getIntegerDefault(overallMap, "processingCount", 0));
            vo.setApprovedCount(getIntegerDefault(overallMap, "approvedCount", 0));
            vo.setRejectedCount(getIntegerDefault(overallMap, "rejectedCount", 0));
            vo.setTodayCount(getIntegerDefault(overallMap, "todayCount", 0));
        } else {
            vo.setTotalCount(0);
            vo.setPendingCount(0);
            vo.setProcessingCount(0);
            vo.setApprovedCount(0);
            vo.setRejectedCount(0);
            vo.setTodayCount(0);
        }

        // 按类型统计
        List<Map<String, Object>> typeList = auditRecordMapper.selectTypeStatistics();
        List<AuditRecordStatisticsVO.TypeStatisticsVO> typeStatistics = new ArrayList<>();
        
        // 先构建所有类型的默认值
        Map<Integer, AuditRecordStatisticsVO.TypeStatisticsVO> typeMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : AUDIT_TYPE_MAP.entrySet()) {
            AuditRecordStatisticsVO.TypeStatisticsVO typeVO = new AuditRecordStatisticsVO.TypeStatisticsVO();
            typeVO.setAuditType(entry.getKey());
            typeVO.setAuditTypeName(entry.getValue());
            typeVO.setCount(0);
            typeVO.setPendingCount(0);
            typeMap.put(entry.getKey(), typeVO);
        }
        
        // 填充实际数据
        if (typeList != null) {
            for (Map<String, Object> map : typeList) {
                Integer auditType = getInteger(map, "auditType");
                if (auditType != null && typeMap.containsKey(auditType)) {
                    AuditRecordStatisticsVO.TypeStatisticsVO typeVO = typeMap.get(auditType);
                    typeVO.setCount(getIntegerDefault(map, "count", 0));
                    typeVO.setPendingCount(getIntegerDefault(map, "pendingCount", 0));
                }
            }
        }
        
        typeStatistics.addAll(typeMap.values());
        vo.setTypeStatistics(typeStatistics);

        return vo;
    }

    // ==================== 私有转换方法 ====================

    private AuditRecordListVO convertToListVO(Map<String, Object> map) {
        AuditRecordListVO vo = new AuditRecordListVO();
        
        vo.setId(getInteger(map, "id"));
        vo.setAuditNo(getString(map, "auditNo"));
        vo.setAuditType(getInteger(map, "auditType"));
        vo.setBusinessId(getInteger(map, "businessId"));
        vo.setTitle(getString(map, "title"));
        
        // 内容截取
        String content = getString(map, "content");
        if (content != null && content.length() > 50) {
            vo.setContent(content.substring(0, 50) + "...");
        } else {
            vo.setContent(content);
        }
        
        vo.setCurrentStatus(getInteger(map, "currentStatus"));
        vo.setPriority(getInteger(map, "priority"));
        vo.setApplicantId(getInteger(map, "applicantId"));
        vo.setApplicantName(getString(map, "applicantName"));
        vo.setApplicantAvatar(getString(map, "applicantAvatar"));
        vo.setSubmitTime(getLocalDateTime(map, "submitTime"));
        vo.setFinishTime(getLocalDateTime(map, "finishTime"));
        vo.setCurrentAdminId(getInteger(map, "currentAdminId"));
        vo.setCurrentAdminName(getString(map, "currentAdminName"));
        
        // 转换名称
        vo.setAuditTypeName(convertAuditTypeName(vo.getAuditType()));
        vo.setStatusName(convertStatusName(vo.getCurrentStatus()));
        vo.setPriorityName(convertPriorityName(vo.getPriority()));
        
        return vo;
    }

    private AuditRecordDetailVO convertToDetailVO(Map<String, Object> map) {
        AuditRecordDetailVO vo = new AuditRecordDetailVO();
        
        vo.setId(getInteger(map, "id"));
        vo.setAuditNo(getString(map, "auditNo"));
        vo.setAuditType(getInteger(map, "auditType"));
        vo.setBusinessId(getInteger(map, "businessId"));
        vo.setTitle(getString(map, "title"));
        vo.setContent(getString(map, "content"));
        vo.setAttachments(getString(map, "attachments"));
        vo.setCurrentStatus(getInteger(map, "currentStatus"));
        vo.setPriority(getInteger(map, "priority"));
        vo.setSubmitTime(getLocalDateTime(map, "submitTime"));
        vo.setFinishTime(getLocalDateTime(map, "finishTime"));
        vo.setFinalOpinion(getString(map, "finalOpinion"));
        vo.setRejectReason(getString(map, "rejectReason"));
        vo.setCurrentAdminId(getInteger(map, "currentAdminId"));
        vo.setCurrentAdminName(getString(map, "currentAdminName"));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        
        // 申请人信息
        vo.setApplicantId(getInteger(map, "applicantId"));
        vo.setApplicantUsername(getString(map, "applicantUsername"));
        vo.setApplicantName(getString(map, "applicantName"));
        vo.setApplicantAvatar(getString(map, "applicantAvatar"));
        
        // 手机号脱敏
        String phone = getString(map, "applicantPhone");
        if (StringUtils.hasText(phone) && phone.length() >= 11) {
            vo.setApplicantPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        } else {
            vo.setApplicantPhone(phone);
        }
        
        // 转换名称
        vo.setAuditTypeName(convertAuditTypeName(vo.getAuditType()));
        vo.setStatusName(convertStatusName(vo.getCurrentStatus()));
        vo.setPriorityName(convertPriorityName(vo.getPriority()));
        
        // 附件列表
        if (StringUtils.hasText(vo.getAttachments())) {
            vo.setAttachmentList(Arrays.asList(vo.getAttachments().split(",")));
        } else {
            vo.setAttachmentList(new ArrayList<>());
        }
        
        return vo;
    }

    private String convertAuditTypeName(Integer auditType) {
        return AUDIT_TYPE_MAP.getOrDefault(auditType, "未知类型");
    }

    private String convertStatusName(Integer status) {
        if (status == null) return null;
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

    private String convertPriorityName(Integer priority) {
        if (priority == null) return "普通";
        switch (priority) {
            case 0: return "低";
            case 1: return "普通";
            case 2: return "高";
            case 3: return "紧急";
            default: return "普通";
        }
    }

    private String convertActionName(Integer action) {
        if (action == null) return "未知";
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

    // ==================== 类型转换工具方法 ====================
    
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerDefault(Map<String, Object> map, String key, Integer defaultValue) {
        Integer result = getInteger(map, key);
        return result != null ? result : defaultValue;
    }

    private LocalDateTime getLocalDateTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return null;
    }
}
