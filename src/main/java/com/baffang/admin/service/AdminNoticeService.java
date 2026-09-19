package com.baffang.admin.service;

import com.baffang.admin.dto.*;
import com.baffang.admin.mapper.SystemNoticeMapper;
import com.baffang.admin.mapper.UserManageMapper;
import com.baffang.common.entity.SystemNotice;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.NoticeTypeEnum;
import com.baffang.common.enums.RelatedTypeEnum;
import com.baffang.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端通知服务
 */
@Slf4j
@Service
public class AdminNoticeService {

    @Autowired
    private SystemNoticeMapper noticeMapper;

    @Autowired
    private UserManageMapper userManageMapper;

    // ==================== 管理端接口调用的方法 ====================

    /**
     * 查询所有通知（分页）
     */
    public AdminNoticePageDTO getNoticeList(AdminNoticeQueryDTO query) {
        List<AdminNoticeDTO> notices = noticeMapper.selectAllNotices(
                query.getType(),
                query.getUserId(),
                query.getIsRead(),
                query.getKeyword(),
                query.getOffset(),
                query.getPageSize()
        );

        notices.forEach(this::fillTypeName);

        int total = noticeMapper.countAllNotices(
                query.getType(),
                query.getUserId(),
                query.getIsRead(),
                query.getKeyword()
        );

        return AdminNoticePageDTO.of(notices, total, query.getPage(), query.getPageSize());
    }

    /**
     * 获取通知详情
     */
    public AdminNoticeDTO getNoticeDetail(Integer noticeId) {
        AdminNoticeDTO notice = noticeMapper.selectDetailById(noticeId);
        if (notice == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }
        fillTypeName(notice);
        return notice;
    }

    /**
     * 发送系统公告（全体用户）
     */
    public void broadcast(BroadcastNoticeDTO dto) {
        SystemNotice notice = new SystemNotice();
        notice.setUserId(null);
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setType(NoticeTypeEnum.SYSTEM_ANNOUNCEMENT.getCode());
        notice.setIsRead(0);
        notice.setCreateTime(new Date());
        notice.setIsDeleted(0);

        noticeMapper.insert(notice);
        log.info("发送系统公告: {}", dto.getTitle());
    }

    /**
     * 发送通知（可指定用户）
     */
    public void sendNotice(SendNoticeDTO dto) {
        if (dto.getUserId() != null) {
            checkUserExists(dto.getUserId());
        }

        SystemNotice notice = new SystemNotice();
        notice.setUserId(dto.getUserId());
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setType(dto.getType());
        notice.setRelatedId(dto.getRelatedId());
        notice.setRelatedType(dto.getRelatedType());
        notice.setIsRead(0);
        notice.setCreateTime(new Date());
        notice.setIsDeleted(0);

        noticeMapper.insert(notice);

        if (dto.getUserId() == null) {
            log.info("发送全体通知: {}", dto.getTitle());
        } else {
            log.info("发送通知给用户 {}: {}", dto.getUserId(), dto.getTitle());
        }
    }

    /**
     * 批量发送通知
     */
    @Transactional
    public void batchSendNotice(BatchSendNoticeDTO dto) {
        if (dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        List<Integer> validUserIds = filterValidUserIds(dto.getUserIds());

        if (validUserIds.isEmpty()) {
            throw new BusinessException("所有指定的用户都不存在");
        }

        if (validUserIds.size() < dto.getUserIds().size()) {
            List<Integer> invalidUserIds = dto.getUserIds().stream()
                    .filter(id -> !validUserIds.contains(id))
                    .collect(Collectors.toList());
            log.warn("以下用户ID不存在，已跳过: {}", invalidUserIds);
        }

        List<SystemNotice> notices = new ArrayList<>();
        for (Integer userId : validUserIds) {
            SystemNotice notice = new SystemNotice();
            notice.setUserId(userId);
            notice.setTitle(dto.getTitle());
            notice.setContent(dto.getContent());
            notice.setType(dto.getType());
            notice.setRelatedId(dto.getRelatedId());
            notice.setRelatedType(dto.getRelatedType());
            notice.setIsRead(0);
            notice.setIsDeleted(0);
            notices.add(notice);
        }

        noticeMapper.batchInsert(notices);
        log.info("批量发送通知给 {} 个用户: {}", validUserIds.size(), dto.getTitle());
    }

    /**
     * 删除通知
     */
    public boolean deleteNotice(Integer noticeId) {
        int rows = noticeMapper.deleteByAdmin(noticeId);
        if (rows > 0) {
            log.info("删除通知: {}", noticeId);
            return true;
        }
        return false;
    }

    /**
     * 批量删除通知
     */
    public int batchDeleteNotice(List<Integer> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return 0;
        }
        int rows = noticeMapper.batchDeleteByAdmin(noticeIds);
        log.info("批量删除通知: {} 条", rows);
        return rows;
    }

    // ==================== 供其他服务调用的核心方法 ====================

    /**
     * 发送通知给指定用户（核心方法，供其他服务调用）
     * 注意：此方法会静默处理用户不存在的情况，不会抛出异常
     */
    public void sendToUser(Integer userId, String title, String content,
                           NoticeTypeEnum type, Integer relatedId, RelatedTypeEnum relatedType) {
        if (userId == null) {
            log.warn("发送通知失败：用户ID为空, title={}", title);
            return;
        }

        // 校验用户是否存在（静默处理，不抛异常）
        if (!userManageMapper.existsById(userId)) {
            log.warn("发送通知失败：用户不存在，userId={}, title={}", userId, title);
            return;
        }

        SystemNotice notice = new SystemNotice();
        notice.setUserId(userId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(type.getCode());
        notice.setRelatedId(relatedId);
        notice.setRelatedType(relatedType != null ? relatedType.getCode() : null);
        notice.setIsRead(0);
        notice.setCreateTime(new Date());
        notice.setIsDeleted(0);

        noticeMapper.insert(notice);
        log.info("发送通知给用户 {}: {}", userId, title);
    }

    // ==================== 各业务场景的便捷方法 ====================

    /**
     * 发送订单通知
     */
    public void sendOrderNotice(Integer userId, Integer orderId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.ORDER, orderId, RelatedTypeEnum.ORDER);
    }

    /**
     * 发送充电通知
     */
    public void sendChargingNotice(Integer userId, Integer orderId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.CHARGING, orderId, RelatedTypeEnum.ORDER);
    }

    /**
     * 发送预约通知
     */
    public void sendReservationNotice(Integer userId, Integer reservationId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.RESERVATION, reservationId, RelatedTypeEnum.RESERVATION);
    }

    /**
     * 发送审核通知（通用）
     */
    public void sendAuditNotice(Integer userId, Integer relatedId, RelatedTypeEnum relatedType,
                                String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.AUDIT, relatedId, relatedType);
    }

    /**
     * 发送紧急呼叫通知
     */
    public void sendEmergencyNotice(Integer userId, Integer callId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.EMERGENCY_CALL, callId, RelatedTypeEnum.EMERGENCY_CALL);
    }

    /**
     * 发送投诉/故障处理通知
     */
    public void sendComplaintNotice(Integer userId, Integer auditId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.AUDIT, auditId, RelatedTypeEnum.STATION);
    }

    /**
     * 发送论坛相关通知（举报结果、内容删除等）
     */
    public void sendForumNotice(Integer userId, Integer businessId, RelatedTypeEnum relatedType,
                                String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.AUDIT, businessId, relatedType);
    }

    /**
     * 发送V2V通知
     */
    public void sendV2VNotice(Integer userId, Integer v2vId, String title, String content) {
        sendToUser(userId, title, content, NoticeTypeEnum.V2V, v2vId, RelatedTypeEnum.V2V_CALL);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验用户是否存在（抛出异常版本，供管理端接口使用）
     */
    private void checkUserExists(Integer userId) {
        if (userId == null) {
            return;
        }
        boolean exists = userManageMapper.existsById(userId);
        if (!exists) {
            throw new BusinessException("用户不存在，用户ID: " + userId);
        }
    }

    /**
     * 过滤出有效的用户ID
     */
    private List<Integer> filterValidUserIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userIds.stream()
                .filter(userId -> userManageMapper.existsById(userId))
                .collect(Collectors.toList());
    }

    /**
     * 填充类型名称
     */
    private void fillTypeName(AdminNoticeDTO dto) {
        dto.setTypeName(NoticeTypeEnum.getDescription(dto.getType()));
        dto.setRelatedTypeName(RelatedTypeEnum.getDescription(dto.getRelatedType()));

        if (dto.getUserId() == null) {
            dto.setUserName("全体用户");
        }
    }
}
