package com.baffang.admin.service.impl;

import com.baffang.admin.dto.StationAuthReviewDTO;
import com.baffang.admin.mapper.AdminMapper;
import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.PrivateChargeStationMapper;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.admin.service.AuditService;
import com.baffang.admin.service.StationAuthService;
import com.baffang.admin.vo.StationAuthDetailVO;
import com.baffang.admin.vo.StationAuthListVO;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.enums.*;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.mapper.UserMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StationAuthServiceImpl implements StationAuthService {

   @Autowired
   private PrivateChargeStationMapper stationMapper;

   @Autowired
   private UserMapper userMapper;

   @Autowired
   private AuditService auditService;

   @Autowired
   private AuditLogMapper auditLogMapper;

   @Autowired
   private AdminMapper adminMapper;

   @Autowired
   private AdminNoticeService noticeService;

   private static final String[] STATUS_NAMES = {"关闭", "开放", "维护中", "审核中"};
   private static final String[] AUTH_STATUS_NAMES = {"未认证", "审核中", "已认证", "认证失败"};
   private static final String[] PRIORITY_NAMES = {"低", "普通", "高", "紧急"};
   private static final String[] PRICE_TYPE_NAMES = {"固定价格", "分时定价"};

   @Override
   public Page<StationAuthListVO> getAuthList(Integer authStatus, Integer pageNum, Integer pageSize) {
       PageHelper.startPage(pageNum, pageSize);
       Page<StationAuthListVO> page = (Page<StationAuthListVO>) stationMapper.selectAuthList(authStatus);
       
       for (StationAuthListVO vo : page) {
           vo.setStatusName(getNameByIndex(vo.getStatus(), STATUS_NAMES));
           vo.setAuthStatusName(getNameByIndex(vo.getAuthStatus(), AUTH_STATUS_NAMES));
           vo.setPriceTypeName(getNameByIndex(vo.getPriceType(), PRICE_TYPE_NAMES));
           
           AuditRecord auditRecord = auditService.getByBusinessIdAndType(
                   vo.getId(), AuditTypeEnum.STATION_AUTH.getCode());
           if (auditRecord != null) {
               vo.setAuditNo(auditRecord.getAuditNo());
               vo.setAuditStatus(auditRecord.getCurrentStatus());
               vo.setAuditStatusName(AuditStatusEnum.getDescByCode(auditRecord.getCurrentStatus()));
           }
       }
       
       return page;
   }

   @Override
   public StationAuthDetailVO getAuthDetail(Integer stationId) {
       StationAuthDetailVO detail = stationMapper.selectAuthDetail(stationId);
       
       if (detail == null) {
           throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
       }
       
       detail.setStatusName(getNameByIndex(detail.getStatus(), STATUS_NAMES));
       detail.setOwnerAuthVehicleName(getNameByIndex(detail.getOwnerAuthVehicle(), AUTH_STATUS_NAMES));
       detail.setOwnerAuthChargingStationName(getNameByIndex(detail.getOwnerAuthChargingStation(), AUTH_STATUS_NAMES));
       detail.setPriceTypeName(getNameByIndex(detail.getPriceType(), PRICE_TYPE_NAMES));
       
       AuditRecord auditRecord = auditService.getByBusinessIdAndType(
               stationId, AuditTypeEnum.STATION_AUTH.getCode());
       
       if (auditRecord != null) {
           StationAuthDetailVO.AuditInfo auditInfo = new StationAuthDetailVO.AuditInfo();
           auditInfo.setAuditId(auditRecord.getId());
           auditInfo.setAuditNo(auditRecord.getAuditNo());
           auditInfo.setCurrentStatus(auditRecord.getCurrentStatus());
           auditInfo.setCurrentStatusName(AuditStatusEnum.getDescByCode(auditRecord.getCurrentStatus()));
           auditInfo.setPriority(auditRecord.getPriority());
           auditInfo.setPriorityName(getNameByIndex(auditRecord.getPriority(), PRIORITY_NAMES));
           auditInfo.setSubmitTime(auditRecord.getSubmitTime());
           auditInfo.setFinishTime(auditRecord.getFinishTime());
           auditInfo.setFinalOpinion(auditRecord.getFinalOpinion());
           auditInfo.setRejectReason(auditRecord.getRejectReason());
           
           List<AuditLog> logs = auditService.getAuditLogs(auditRecord.getId());
           List<StationAuthDetailVO.AuditLogInfo> logInfos = new ArrayList<>();
           
           for (AuditLog auditLog : logs) {
               StationAuthDetailVO.AuditLogInfo logInfo = new StationAuthDetailVO.AuditLogInfo();
               logInfo.setAction(auditLog.getAction());
               logInfo.setActionName(AuditActionEnum.getDescByCode(auditLog.getAction()));
               logInfo.setFromStatus(auditLog.getFromStatus());
               logInfo.setFromStatusName(AuditStatusEnum.getDescByCode(auditLog.getFromStatus()));
               logInfo.setToStatus(auditLog.getToStatus());
               logInfo.setToStatusName(AuditStatusEnum.getDescByCode(auditLog.getToStatus()));
               logInfo.setOpinion(auditLog.getOpinion());
               logInfo.setCreateTime(auditLog.getCreateTime());
               
               if (auditLog.getAdminId() != null) {
                   AdminInfo admin = adminMapper.selectById(Long.valueOf(auditLog.getAdminId()));
                   if (admin != null) {
                       logInfo.setAdminName(admin.getName());
                   }
               }
               
               logInfos.add(logInfo);
           }
           
           auditInfo.setLogs(logInfos);
           detail.setAuditInfo(auditInfo);
       }
       
       return detail;
   }

   @Override
   @Transactional(rollbackFor = Exception.class)
   public void reviewAuth(StationAuthReviewDTO dto, Integer adminId) {
       // 参数校验
       if (dto.getStationId() == null) {
           throw new BusinessException(ErrorMsg.PARAM_ERROR);
       }
       if (dto.getAuthStatus() == null || 
           (dto.getAuthStatus() != 2 && dto.getAuthStatus() != 3 && dto.getAuthStatus() != 4)) {
           throw new BusinessException(ErrorMsg.PARAM_ERROR);
       }
       if (dto.getAuthStatus() == 3 && 
           (dto.getRejectReason() == null || dto.getRejectReason().trim().isEmpty())) {
           throw new BusinessException(ErrorMsg.REMARK_REQUIRED);
       }

       // 查询私桩信息
       Map<String, Object> station = stationMapper.selectById(dto.getStationId());
       if (station == null) {
           throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
       }

       // 获取审核记录
       AuditRecord auditRecord = auditService.getByBusinessIdAndType(
               dto.getStationId(), AuditTypeEnum.STATION_AUTH.getCode());
       
       if (auditRecord == null) {
           throw new BusinessException(ErrorMsg.AUDIT_RECORD_NOT_FOUND);
       }
       
       if (auditRecord.getCurrentStatus() != AuditStatusEnum.PENDING.getCode() &&
           auditRecord.getCurrentStatus() != AuditStatusEnum.PROCESSING.getCode()) {
           throw new BusinessException(ErrorMsg.AUDIT_STATUS_INVALID);
       }

       // 兼容下划线命名获取字段
       Integer ownerId = getIntegerValue(station, "owner_id", "ownerId");
       String stationName = getStringValue(station, "name", "station_name", "stationName");
       
       log.info("审核私桩: stationId={}, ownerId={}, stationName={}", dto.getStationId(), ownerId, stationName);

       // 根据审核结果处理
       switch (dto.getAuthStatus()) {
           case 2: // 通过
               auditService.approve(auditRecord.getId(), adminId, dto.getOpinion());
               stationMapper.updateAuthStatus(dto.getStationId(), 2);
               sendStationAuditNotice(ownerId, dto.getStationId(), stationName, true, null);
               break;
           case 3: // 拒绝
               auditService.reject(auditRecord.getId(), adminId, dto.getOpinion(), dto.getRejectReason());
               stationMapper.updateAuthStatus(dto.getStationId(), 3);
               sendStationAuditNotice(ownerId, dto.getStationId(), stationName, false, dto.getRejectReason());
               break;
           case 4: // 需补充材料
               auditService.requireSupplement(auditRecord.getId(), adminId, dto.getOpinion());
               sendStationSupplementNotice(ownerId, dto.getStationId(), stationName, dto.getOpinion());
               break;
       }

       // 更新用户的私桩认证状态
       if (ownerId != null) {
           if (dto.getAuthStatus() == 2 || dto.getAuthStatus() == 3) {
               try {
                   userMapper.updateAuthChargingStation(ownerId, dto.getAuthStatus());
               } catch (Exception e) {
                   log.error("更新用户私桩认证状态失败：{}", e.getMessage());
               }
           }
       } else {
           log.warn("ownerId 为空，无法发送通知，stationId={}", dto.getStationId());
       }
   }

   // ==================== 发送通知的私有方法 ====================

   /**
    * 发送私桩审核结果通知（通过/拒绝）
    */
   private void sendStationAuditNotice(Integer userId, Integer stationId, String stationName, 
                                       boolean approved, String rejectReason) {
       if (userId == null) {
           log.warn("发送私桩审核通知失败：userId 为空，stationId={}", stationId);
           return;
       }
       
       try {
           String title;
           String content;
           String displayName = (stationName != null && !stationName.isEmpty()) ? stationName : "您的充电站";
           
           if (approved) {
               title = "充电站认证通过";
               content = String.format("恭喜！「%s」已通过认证审核，现在可以正常运营了。", displayName);
           } else {
               title = "充电站认证未通过";
               content = String.format("很抱歉，「%s」未通过认证审核。原因：%s", 
                       displayName,
                       rejectReason != null ? rejectReason : "不符合认证要求");
           }
           
           noticeService.sendAuditNotice(userId, stationId, RelatedTypeEnum.STATION, title, content);
           log.info("发送私桩审核通知成功: userId={}, stationId={}, approved={}", userId, stationId, approved);
       } catch (Exception e) {
           log.error("发送私桩审核通知失败: userId={}, stationId={}", userId, stationId, e);
       }
   }

   /**
    * 发送需补充材料通知
    */
   private void sendStationSupplementNotice(Integer userId, Integer stationId, String stationName, String opinion) {
       if (userId == null) {
           log.warn("发送补充材料通知失败：userId 为空，stationId={}", stationId);
           return;
       }
       
       try {
           String displayName = (stationName != null && !stationName.isEmpty()) ? stationName : "您的充电站";
           
           noticeService.sendAuditNotice(
                   userId, 
                   stationId, 
                   RelatedTypeEnum.STATION,
                   "充电站认证需补充材料",
                   String.format("「%s」认证审核需要补充材料。%s", 
                           displayName,
                           opinion != null ? "说明：" + opinion : "请登录查看详情。")
           );
           log.info("发送补充材料通知成功: userId={}, stationId={}", userId, stationId);
       } catch (Exception e) {
           log.error("发送私桩补充材料通知失败: userId={}, stationId={}", userId, stationId, e);
       }
   }

   // ==================== 工具方法 ====================

   private Integer getIntegerValue(Map<String, Object> map, String... keys) {
       for (String key : keys) {
           Object value = map.get(key);
           if (value != null) {
               if (value instanceof Integer) return (Integer) value;
               if (value instanceof Long) return ((Long) value).intValue();
               if (value instanceof Number) return ((Number) value).intValue();
               try {
                   return Integer.parseInt(value.toString());
               } catch (NumberFormatException ignored) {}
           }
       }
       return null;
   }

   private String getStringValue(Map<String, Object> map, String... keys) {
       for (String key : keys) {
           Object value = map.get(key);
           if (value != null) {
               return value.toString();
           }
       }
       return null;
   }

   private String getNameByIndex(Integer index, String[] names) {
       if (index == null || index < 0 || index >= names.length) {
           return "未知";
       }
       return names[index];
   }
}
