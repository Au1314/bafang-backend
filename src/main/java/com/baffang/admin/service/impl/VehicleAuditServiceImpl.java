package com.baffang.admin.service.impl;

import com.baffang.admin.dto.VehicleAuditQueryDTO;
import com.baffang.admin.dto.VehicleAuditReviewDTO;
import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.mapper.VehicleAuditMapper;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.admin.service.VehicleAuditService;
import com.baffang.admin.vo.VehicleAuditDetailVO;
import com.baffang.admin.vo.VehicleAuditListVO;
import com.baffang.admin.vo.VehicleAuditStatisticsVO;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.RelatedTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VehicleAuditServiceImpl implements VehicleAuditService {

    @Autowired
    private VehicleAuditMapper vehicleAuditMapper;

    @Autowired
    private AuditRecordMapper auditRecordMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private AdminNoticeService noticeService;  // 新增

    @Override
    public Page<VehicleAuditListVO> getAuditList(VehicleAuditQueryDTO query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return (Page<VehicleAuditListVO>) vehicleAuditMapper.selectAuditList(query);
    }

    @Override
    public VehicleAuditDetailVO getAuditDetail(Integer id) {
        // 查询审核记录
        Map<String, Object> auditData = vehicleAuditMapper.selectAuditDetail(id);
        if (auditData == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }

        VehicleAuditDetailVO result = new VehicleAuditDetailVO();

        // 审核信息
        VehicleAuditDetailVO.AuditInfo auditInfo = new VehicleAuditDetailVO.AuditInfo();
        auditInfo.setId(getInteger(auditData, "id"));
        auditInfo.setAuditNo(getString(auditData, "auditNo"));
        auditInfo.setCurrentStatus(getInteger(auditData, "currentStatus"));
        auditInfo.setPriority(getInteger(auditData, "priority"));
        auditInfo.setTitle(getString(auditData, "title"));
        auditInfo.setContent(getString(auditData, "content"));
        auditInfo.setAttachments(getString(auditData, "attachments"));
        auditInfo.setSubmitTime(getDate(auditData, "submitTime"));
        auditInfo.setFinishTime(getDate(auditData, "finishTime"));
        auditInfo.setCurrentAdminId(getInteger(auditData, "currentAdminId"));
        auditInfo.setCurrentAdminName(getString(auditData, "currentAdminName"));
        auditInfo.setFinalOpinion(getString(auditData, "finalOpinion"));
        auditInfo.setRejectReason(getString(auditData, "rejectReason"));
        result.setAuditInfo(auditInfo);

        // 申请人信息
        Integer applicantId = getInteger(auditData, "applicantId");
        if (applicantId != null) {
            Map<String, Object> applicantData = vehicleAuditMapper.selectApplicantInfo(applicantId);
            if (applicantData != null) {
                VehicleAuditDetailVO.ApplicantInfo applicantInfo = new VehicleAuditDetailVO.ApplicantInfo();
                applicantInfo.setId(getInteger(applicantData, "id"));
                applicantInfo.setName(getString(applicantData, "name"));
                applicantInfo.setPhone(getString(applicantData, "phone"));
                applicantInfo.setAvatar(getString(applicantData, "avatar"));
                applicantInfo.setAuthVehicle(getInteger(applicantData, "authVehicle"));
                result.setApplicantInfo(applicantInfo);
            }
        }

        // 车辆信息
        Integer businessId = getInteger(auditData, "businessId");
        if (businessId != null) {
            Map<String, Object> vehicleData = vehicleAuditMapper.selectVehicleInfo(businessId);
            if (vehicleData != null) {
                VehicleAuditDetailVO.VehicleInfo vehicleInfo = new VehicleAuditDetailVO.VehicleInfo();
                vehicleInfo.setId(getInteger(vehicleData, "id"));
                vehicleInfo.setVehicleNumber(getString(vehicleData, "vehicleNumber"));
                vehicleInfo.setCarName(getString(vehicleData, "carName"));
                vehicleInfo.setCarBrand(getString(vehicleData, "carBrand"));
                vehicleInfo.setCarType(getString(vehicleData, "carType"));
                vehicleInfo.setVehicleType(getString(vehicleData, "vehicleType"));
                vehicleInfo.setCurrentType(getInteger(vehicleData, "currentType"));
                vehicleInfo.setMaxChargeVoltage(getInteger(vehicleData, "maxChargeVoltage"));
                vehicleInfo.setMaxChargeCurrent(getInteger(vehicleData, "maxChargeCurrent"));
                vehicleInfo.setMaxChargePower(getInteger(vehicleData, "maxChargePower"));
                
                Object batteryCapacity = vehicleData.get("batteryCapacity");
                if (batteryCapacity instanceof BigDecimal) {
                    vehicleInfo.setBatteryCapacity((BigDecimal) batteryCapacity);
                } else if (batteryCapacity != null) {
                    vehicleInfo.setBatteryCapacity(new BigDecimal(batteryCapacity.toString()));
                }
                
                vehicleInfo.setDischargePower(getInteger(vehicleData, "dischargePower"));
                vehicleInfo.setRange(getInteger(vehicleData, "range"));
                vehicleInfo.setAuthStatus(getInteger(vehicleData, "authStatus"));
                vehicleInfo.setCreateTime(getDate(vehicleData, "createTime"));
                result.setVehicleInfo(vehicleInfo);
            }
        }

        // 审核日志
        List<Map<String, Object>> logsData = vehicleAuditMapper.selectAuditLogs(id);
        List<VehicleAuditDetailVO.AuditLogInfo> auditLogs = new ArrayList<>();
        for (Map<String, Object> logData : logsData) {
            VehicleAuditDetailVO.AuditLogInfo logInfo = new VehicleAuditDetailVO.AuditLogInfo();
            logInfo.setId(getInteger(logData, "id"));
            logInfo.setAdminId(getInteger(logData, "adminId"));
            logInfo.setAdminName(getString(logData, "adminName"));
            logInfo.setAction(getInteger(logData, "action"));
            logInfo.setActionText(convertActionText(getInteger(logData, "action")));
            logInfo.setFromStatus(getInteger(logData, "fromStatus"));
            logInfo.setToStatus(getInteger(logData, "toStatus"));
            logInfo.setOpinion(getString(logData, "opinion"));
            logInfo.setCreateTime(getDate(logData, "createTime"));
            auditLogs.add(logInfo);
        }
        result.setAuditLogs(auditLogs);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimAudit(Integer id, Integer adminId) {
        // 查询审核记录
        AuditRecord record = auditRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }

        // 检查审核类型
        if (record.getAuditType() != 1) {
            throw new BusinessException("该记录不是车主认证审核");
        }

        // 检查状态，只有待审核状态才能认领
        if (record.getCurrentStatus() != 0) {
            throw new BusinessException("当前状态不允许认领");
        }

        // 更新审核记录
        AuditRecord updateRecord = new AuditRecord();
        updateRecord.setId(id);
        updateRecord.setCurrentStatus(1); // 审核中
        updateRecord.setCurrentAdminId(adminId);
        auditRecordMapper.updateById(updateRecord);

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(id);
        auditLog.setAdminId(adminId);
        auditLog.setAction(0); // 认领使用0-提交审核
        auditLog.setFromStatus(0);
        auditLog.setToStatus(1);
        auditLog.setOpinion("认领审核任务");
        auditLogMapper.insert(auditLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(VehicleAuditReviewDTO dto, Integer adminId) {
        // 查询审核记录
        AuditRecord record = auditRecordMapper.selectById(dto.getAuditId());
        if (record == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }

        // 检查审核类型
        if (record.getAuditType() != 1) {
            throw new BusinessException("该记录不是车主认证审核");
        }

        // 检查状态
        if (record.getCurrentStatus() != 0 && record.getCurrentStatus() != 1) {
            throw new BusinessException("当前状态不允许审核");
        }

        Integer fromStatus = record.getCurrentStatus();
        Integer toStatus;
        Integer carAuthStatus;
        Integer userAuthVehicle;

        switch (dto.getAction()) {
            case 1: // 通过
                toStatus = 2;
                carAuthStatus = 2; // 已认证
                userAuthVehicle = 2; // 已认证
                break;
            case 2: // 拒绝
                if (dto.getRejectReason() == null || dto.getRejectReason().isEmpty()) {
                    throw new BusinessException("拒绝时必须填写拒绝原因");
                }
                toStatus = 3;
                carAuthStatus = 3; // 认证失败
                userAuthVehicle = 3; // 认证失败
                break;
            case 3: // 要求补充材料
                toStatus = 4;
                carAuthStatus = 1; // 保持审核中
                userAuthVehicle = 1; // 保持审核中
                break;
            default:
                throw new BusinessException("无效的审核操作");
        }

        // 更新审核记录
        AuditRecord updateRecord = new AuditRecord();
        updateRecord.setId(dto.getAuditId());
        updateRecord.setCurrentStatus(toStatus);
        updateRecord.setCurrentAdminId(adminId);
        updateRecord.setFinalOpinion(dto.getOpinion());
        updateRecord.setRejectReason(dto.getRejectReason());
        
        // 如果是通过或拒绝，设置完成时间
        if (dto.getAction() == 1 || dto.getAction() == 2) {
            updateRecord.setFinishTime(new Date());
        }
        
        auditRecordMapper.updateById(updateRecord);

        // 更新车辆认证状态
        vehicleAuditMapper.updateCarAuthStatus(record.getBusinessId(), carAuthStatus);

        // 更新用户认证状态
        vehicleAuditMapper.updateUserAuthVehicle(record.getApplicantId(), userAuthVehicle);

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(dto.getAuditId());
        auditLog.setAdminId(adminId);
        auditLog.setAction(dto.getAction());
        auditLog.setFromStatus(fromStatus);
        auditLog.setToStatus(toStatus);
        auditLog.setOpinion(dto.getOpinion());
        auditLogMapper.insert(auditLog);

        log.info("车主认证审核完成: auditId={}, action={}, adminId={}", dto.getAuditId(), dto.getAction(), adminId);

        // ========== 发送通知 ==========
        sendVehicleAuditNotice(record.getApplicantId(), record.getBusinessId(), dto.getAction(), 
                dto.getOpinion(), dto.getRejectReason());
    }

    /**
     * 发送车辆认证审核通知
     */
    private void sendVehicleAuditNotice(Integer applicantId, Integer vehicleId, 
                                         Integer action, String opinion, String rejectReason) {
        if (applicantId == null) {
            log.warn("车辆认证通知发送失败：申请人ID为空");
            return;
        }

        // 获取车辆信息用于通知内容
        String vehicleInfo = "";
        if (vehicleId != null) {
            Map<String, Object> vehicleData = vehicleAuditMapper.selectVehicleInfo(vehicleId);
            if (vehicleData != null) {
                String vehicleNumber = getString(vehicleData, "vehicleNumber");
                String carName = getString(vehicleData, "carName");
                vehicleInfo = String.format("（%s %s）", 
                        carName != null ? carName : "", 
                        vehicleNumber != null ? vehicleNumber : "");
            }
        }

        String title;
        String content;

        switch (action) {
            case 1: // 通过
                title = "车主认证审核通过";
                content = String.format("恭喜您！您的车主认证申请%s已通过审核，现在可以享受完整的充电服务了。%s",
                        vehicleInfo,
                        opinion != null ? "审核意见：" + opinion : "");
                break;
                
            case 2: // 拒绝
                title = "车主认证审核未通过";
                content = String.format("很抱歉，您的车主认证申请%s未通过审核。原因：%s。请核实信息后重新提交申请。",
                        vehicleInfo,
                        rejectReason != null ? rejectReason : "资料不符合要求");
                break;
                
            case 3: // 要求补充材料
                title = "车主认证需要补充材料";
                content = String.format("您的车主认证申请%s需要补充材料。%s请尽快补充相关材料以完成认证。",
                        vehicleInfo,
                        opinion != null ? "说明：" + opinion + "。" : "");
                break;
                
            default:
                log.warn("未知的审核操作类型: {}", action);
                return;
        }

        try {
            noticeService.sendAuditNotice(
                    applicantId,
                    vehicleId,
                    RelatedTypeEnum.VEHICLE,  // 需要确保枚举中有 VEHICLE 类型
                    title,
                    content
            );
            log.info("车辆认证通知发送成功: applicantId={}, action={}", applicantId, action);
        } catch (Exception e) {
            log.error("车辆认证通知发送失败: applicantId={}, action={}", applicantId, action, e);
        }
    }

    @Override
    public VehicleAuditStatisticsVO getStatistics() {
        VehicleAuditStatisticsVO statistics = vehicleAuditMapper.selectStatistics();
        if (statistics == null) {
            statistics = new VehicleAuditStatisticsVO();
            statistics.setPending(0);
            statistics.setProcessing(0);
            statistics.setApproved(0);
            statistics.setRejected(0);
            statistics.setNeedSupplement(0);
        }
        statistics.setTodayProcessed(vehicleAuditMapper.selectTodayProcessedCount());
        return statistics;
    }

    // ==================== 工具方法 ====================

    /**
     * 转换操作类型文本
     */
    private String convertActionText(Integer action) {
        if (action == null) return "未知操作";
        switch (action) {
            case 0: return "提交审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            case 3: return "要求补充材料";
            case 4: return "转交他人";
            case 5: return "撤回";
            case 6: return "重新提交";
            default: return "未知操作";
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? null : value.toString();
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    /**
     * 安全地从 Map 中获取 Date 类型
     * 支持 Date、LocalDateTime、Timestamp 类型的自动转换
     */
    private Date getDate(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime ldt = (LocalDateTime) value;
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        if (value instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) value).getTime());
        }
        return null;
    }
}
