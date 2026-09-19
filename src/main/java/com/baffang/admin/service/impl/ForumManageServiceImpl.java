package com.baffang.admin.service.impl;

import com.baffang.admin.dto.*;
import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.ForumPostMapper;
import com.baffang.admin.mapper.ForumReplyMapper;
import com.baffang.admin.mapper.ForumReportMapper;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.admin.service.ForumManageService;
import com.baffang.admin.vo.*;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.enums.RelatedTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumManageServiceImpl implements ForumManageService {

    private final ForumPostMapper forumPostMapper;
    private final ForumReplyMapper forumReplyMapper;
    private final ForumReportMapper forumReportMapper;
    private final AuditLogMapper auditLogMapper;
    private final AdminNoticeService noticeService;  // 新增
    
    // JSON 解析器
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 分页参数工具方法 ====================
    
    private int getValidPageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int getValidPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    // ==================== 帖子管理 ====================

    @Override
    public Page<ForumPostListVO> getPostList(ForumPostQueryDTO queryDTO) {
        int pageNum = getValidPageNum(queryDTO != null ? queryDTO.getPageNum() : null);
        int pageSize = getValidPageSize(queryDTO != null ? queryDTO.getPageSize() : null);
        
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = forumPostMapper.selectList(queryDTO);
        
        Page<ForumPostListVO> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPages(((Page<?>) mapList).getPages());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        for (Map<String, Object> map : mapList) {
            ForumPostListVO vo = convertToPostListVO(map);
            page.add(vo);
        }

        return page;
    }

    @Override
    public ForumPostDetailVO getPostDetail(Integer id) {
        Map<String, Object> map = forumPostMapper.selectDetailById(id);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("帖子不存在");
        }

        ForumPostDetailVO vo = convertToPostDetailVO(map);
        
        List<Map<String, Object>> replyMaps = forumReplyMapper.selectLatestByPostId(id, 5);
        List<ForumReplyListVO> latestReplies = new ArrayList<>();
        for (Map<String, Object> replyMap : replyMaps) {
            latestReplies.add(convertToReplyListVO(replyMap));
        }
        vo.setLatestReplies(latestReplies);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Integer id, Integer adminId) {
        if (!forumPostMapper.existsById(id)) {
            throw new BusinessException("帖子不存在");
        }
        int deletedReplies = forumPostMapper.deleteRepliesByPostId(id);
        log.info("删除帖子[{}]下的评论，共{}条", id, deletedReplies);
        
        forumPostMapper.deleteById(id);
        log.info("管理员[{}]删除帖子[{}]", adminId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePost(List<Integer> ids, Integer adminId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的帖子");
        }
        int deletedReplies = forumPostMapper.batchDeleteRepliesByPostIds(ids);
        log.info("批量删除帖子{}下的评论，共{}条", ids, deletedReplies);
        
        forumPostMapper.batchDelete(ids);
        log.info("管理员[{}]批量删除帖子：{}", adminId, ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTop(Integer id, Integer isTop, Integer adminId) {
        if (!forumPostMapper.existsById(id)) {
            throw new BusinessException("帖子不存在");
        }
        if (isTop != 0 && isTop != 1) {
            throw new BusinessException("置顶状态值无效");
        }
        forumPostMapper.updateTop(id, isTop);
        log.info("管理员[{}]{}帖子[{}]", adminId, isTop == 1 ? "置顶" : "取消置顶", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEssence(Integer id, Integer isEssence, Integer adminId) {
        if (!forumPostMapper.existsById(id)) {
            throw new BusinessException("帖子不存在");
        }
        if (isEssence != 0 && isEssence != 1) {
            throw new BusinessException("精华状态值无效");
        }
        forumPostMapper.updateEssence(id, isEssence);
        log.info("管理员[{}]{}帖子[{}]", adminId, isEssence == 1 ? "设为精华" : "取消精华", id);
    }

    // ==================== 评论管理 ====================

    @Override
    public Page<ForumReplyListVO> getReplyList(ForumReplyQueryDTO queryDTO) {
        int pageNum = getValidPageNum(queryDTO != null ? queryDTO.getPageNum() : null);
        int pageSize = getValidPageSize(queryDTO != null ? queryDTO.getPageSize() : null);
        
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = forumReplyMapper.selectList(queryDTO);
        
        Page<ForumReplyListVO> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPages(((Page<?>) mapList).getPages());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        for (Map<String, Object> map : mapList) {
            page.add(convertToReplyListVO(map));
        }

        return page;
    }

    @Override
    public ForumReplyDetailVO getReplyDetail(Integer id) {
        Map<String, Object> map = forumReplyMapper.selectDetailById(id);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("评论不存在");
        }

        ForumReplyDetailVO vo = convertToReplyDetailVO(map);
        
        List<Map<String, Object>> childMaps = forumReplyMapper.selectChildReplies(id);
        List<ForumReplyListVO> childReplies = new ArrayList<>();
        for (Map<String, Object> childMap : childMaps) {
            childReplies.add(convertToReplyListVO(childMap));
        }
        vo.setChildReplies(childReplies);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReply(Integer id, Integer adminId) {
        if (!forumReplyMapper.existsById(id)) {
            throw new BusinessException("评论不存在");
        }
        forumReplyMapper.deleteById(id);
        log.info("管理员[{}]删除评论[{}]", adminId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteReply(List<Integer> ids, Integer adminId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的评论");
        }
        forumReplyMapper.batchDelete(ids);
        log.info("管理员[{}]批量删除评论：{}", adminId, ids);
    }

    // ==================== 举报审核 ====================

    @Override
    public Page<ForumReportListVO> getPostReportList(ForumReportQueryDTO queryDTO) {
        int pageNum = getValidPageNum(queryDTO != null ? queryDTO.getPageNum() : null);
        int pageSize = getValidPageSize(queryDTO != null ? queryDTO.getPageSize() : null);
        
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = forumReportMapper.selectPostReportList(queryDTO);
        
        return convertToReportPage(mapList, pageNum, pageSize);
    }

    @Override
    public Page<ForumReportListVO> getReplyReportList(ForumReportQueryDTO queryDTO) {
        int pageNum = getValidPageNum(queryDTO != null ? queryDTO.getPageNum() : null);
        int pageSize = getValidPageSize(queryDTO != null ? queryDTO.getPageSize() : null);
        
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = forumReportMapper.selectReplyReportList(queryDTO);
        
        return convertToReportPage(mapList, pageNum, pageSize);
    }

    private Page<ForumReportListVO> convertToReportPage(List<Map<String, Object>> mapList, int pageNum, int pageSize) {
        Page<ForumReportListVO> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPages(((Page<?>) mapList).getPages());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        for (Map<String, Object> map : mapList) {
            page.add(convertToReportListVO(map));
        }

        return page;
    }

    @Override
    public ForumReportDetailVO getReportDetail(Integer id) {
        Map<String, Object> map = forumReportMapper.selectDetailById(id);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("举报记录不存在");
        }

        ForumReportDetailVO vo = convertToReportDetailVO(map);
        
        Integer auditType = getInteger(map, "auditType");
        Integer businessId = getInteger(map, "businessId");
        
        if (auditType != null && businessId != null) {
            if (auditType == 5) {
                Map<String, Object> postMap = forumPostMapper.selectDetailById(businessId);
                if (postMap != null) {
                    vo.setReportedContent(getString(postMap, "title") + " - " + getString(postMap, "content"));
                    vo.setReportedUserId(getInteger(postMap, "userId"));
                    vo.setReportedUserName(getString(postMap, "userName"));
                    vo.setReportedUserAvatar(getString(postMap, "userAvatar"));
                    vo.setReportedPostImages(parseJsonToList(getString(postMap, "images")));
                }
            } else if (auditType == 4) {
                Map<String, Object> replyMap = forumReplyMapper.selectDetailById(businessId);
                if (replyMap != null) {
                    vo.setReportedContent(getString(replyMap, "content"));
                    vo.setReportedUserId(getInteger(replyMap, "userId"));
                    vo.setReportedUserName(getString(replyMap, "userName"));
                    vo.setReportedUserAvatar(getString(replyMap, "userAvatar"));
                }
            }
        }

        List<Map<String, Object>> logMaps = auditLogMapper.selectLogsWithAdmin(id);
        List<AuditLogVO> auditLogs = new ArrayList<>();
        for (Map<String, Object> logMap : logMaps) {
            auditLogs.add(convertToAuditLogVO(logMap));
        }
        vo.setAuditLogs(auditLogs);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveReport(Integer id, ForumReportAuditDTO dto, Integer adminId) {
        if (!forumReportMapper.existsById(id)) {
            throw new BusinessException("举报记录不存在");
        }

        Integer currentStatus = forumReportMapper.selectCurrentStatus(id);
        if (currentStatus == null) {
            throw new BusinessException("举报记录不存在");
        }
        if (currentStatus == 2) {
            throw new BusinessException("该记录已审核通过");
        }
        if (currentStatus == 3) {
            throw new BusinessException("该记录已被拒绝");
        }

        LocalDateTime now = LocalDateTime.now();
        String opinion = StringUtils.hasText(dto.getOpinion()) ? dto.getOpinion() : "举报成立，已处理";

        // 更新审核状态
        forumReportMapper.updateStatusApproved(id, adminId, opinion, now);

        // 获取举报信息，用于发送通知
        Map<String, Object> businessInfo = forumReportMapper.selectBusinessInfo(id);
        Integer applicantId = getInteger(businessInfo, "applicantId");
        Integer reportedUserId = getInteger(businessInfo, "reportedUserId");
        Integer auditType = getInteger(businessInfo, "auditType");
        Integer businessId = getInteger(businessInfo, "businessId");

        // 如果需要删除被举报内容
        if (Boolean.TRUE.equals(dto.getDeleteContent())) {
            if (auditType == 5 && businessId != null) {
                forumPostMapper.deleteById(businessId);
                log.info("删除被举报帖子[{}]", businessId);
            } else if (auditType == 4 && businessId != null) {
                forumReplyMapper.deleteById(businessId);
                log.info("删除被举报评论[{}]", businessId);
            }
        }

        // 记录日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(id);
        auditLog.setAdminId(adminId);
        auditLog.setAction(1);
        auditLog.setFromStatus(currentStatus);
        auditLog.setToStatus(2);
        auditLog.setOpinion(opinion);
        auditLogMapper.insert(auditLog);

        log.info("管理员[{}]审核通过举报[{}]", adminId, id);

        // ========== 发送通知 ==========
        sendReportApproveNotice(applicantId, reportedUserId, auditType, dto.getDeleteContent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReport(Integer id, ForumReportAuditDTO dto, Integer adminId) {
        if (!forumReportMapper.existsById(id)) {
            throw new BusinessException("举报记录不存在");
        }

        Integer currentStatus = forumReportMapper.selectCurrentStatus(id);
        if (currentStatus == null) {
            throw new BusinessException("举报记录不存在");
        }
        if (currentStatus == 2) {
            throw new BusinessException("该记录已审核通过");
        }
        if (currentStatus == 3) {
            throw new BusinessException("该记录已被拒绝");
        }

        LocalDateTime now = LocalDateTime.now();
        String opinion = StringUtils.hasText(dto.getOpinion()) ? dto.getOpinion() : "举报不成立";
        String rejectReason = StringUtils.hasText(dto.getRejectReason()) ? dto.getRejectReason() : "经核实，举报内容不成立";

        // 更新审核状态
        forumReportMapper.updateStatusRejected(id, adminId, opinion, rejectReason, now);

        // 获取举报信息
        Map<String, Object> businessInfo = forumReportMapper.selectBusinessInfo(id);
        Integer applicantId = getInteger(businessInfo, "applicantId");
        Integer auditType = getInteger(businessInfo, "auditType");

        // 记录日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(id);
        auditLog.setAdminId(adminId);
        auditLog.setAction(2);
        auditLog.setFromStatus(currentStatus);
        auditLog.setToStatus(3);
        auditLog.setOpinion(opinion);
        auditLogMapper.insert(auditLog);

        log.info("管理员[{}]审核拒绝举报[{}]", adminId, id);

        // ========== 发送通知给举报人 ==========
        sendReportRejectNotice(applicantId, auditType, rejectReason);
    }

    // ==================== 发送通知的私有方法 ====================

    /**
     * 发送举报通过通知
     */
    private void sendReportApproveNotice(Integer applicantId, Integer reportedUserId, 
                                          Integer auditType, Boolean deleted) {
        String typeName = auditType == 5 ? "帖子" : "评论";
        RelatedTypeEnum relatedType = auditType == 5 ? RelatedTypeEnum.POST : RelatedTypeEnum.REVIEW;
        
        // 通知举报人
        if (applicantId != null) {
            try {
                noticeService.sendForumNotice(
                        applicantId,
                        null,
                        relatedType,
                        "举报处理结果",
                        String.format("您举报的%s经核实确实存在违规，%s感谢您的反馈。",
                                typeName,
                                Boolean.TRUE.equals(deleted) ? "已被删除，" : "已做处理，")
                );
            } catch (Exception e) {
                log.error("发送举报结果通知失败: applicantId={}", applicantId, e);
            }
        }
        
        // 通知被举报人（如果内容被删除）
        if (reportedUserId != null && Boolean.TRUE.equals(deleted)) {
            try {
                noticeService.sendForumNotice(
                        reportedUserId,
                        null,
                        relatedType,
                        typeName + "违规处理通知",
                        String.format("您发布的%s因违反社区规定已被删除，请遵守社区规范。", typeName)
                );
            } catch (Exception e) {
                log.error("发送违规通知失败: reportedUserId={}", reportedUserId, e);
            }
        }
    }

    /**
     * 发送举报拒绝通知
     */
    private void sendReportRejectNotice(Integer applicantId, Integer auditType, String reason) {
        if (applicantId == null) return;
        
        try {
            String typeName = auditType == 5 ? "帖子" : "评论";
            RelatedTypeEnum relatedType = auditType == 5 ? RelatedTypeEnum.POST : RelatedTypeEnum.REVIEW;
            
            noticeService.sendForumNotice(
                    applicantId,
                    null,
                    relatedType,
                    "举报处理结果",
                    String.format("您举报的%s经核实不存在违规行为。%s",
                            typeName,
                            reason != null ? "说明：" + reason : "")
            );
        } catch (Exception e) {
            log.error("发送举报拒绝通知失败: applicantId={}", applicantId, e);
        }
    }

    // ==================== 统计 ====================

    @Override
    public ForumStatisticsVO getStatistics() {
        ForumStatisticsVO vo = new ForumStatisticsVO();

        Map<String, Object> postStats = forumPostMapper.selectStatistics();
        if (postStats != null) {
            vo.setTotalPostCount(getIntegerDefault(postStats, "totalPostCount", 0));
            vo.setTodayPostCount(getIntegerDefault(postStats, "todayPostCount", 0));
            vo.setTopPostCount(getIntegerDefault(postStats, "topPostCount", 0));
            vo.setEssencePostCount(getIntegerDefault(postStats, "essencePostCount", 0));
        }

        Map<String, Object> replyStats = forumReplyMapper.selectStatistics();
        if (replyStats != null) {
            vo.setTotalReplyCount(getIntegerDefault(replyStats, "totalReplyCount", 0));
            vo.setTodayReplyCount(getIntegerDefault(replyStats, "todayReplyCount", 0));
        }

        Map<String, Object> reportStats = forumReportMapper.selectStatistics();
        if (reportStats != null) {
            vo.setPendingPostReportCount(getIntegerDefault(reportStats, "pendingPostReportCount", 0));
            vo.setPendingReplyReportCount(getIntegerDefault(reportStats, "pendingReplyReportCount", 0));
            vo.setMonthProcessedReportCount(getIntegerDefault(reportStats, "monthProcessedReportCount", 0));
        }

        return vo;
    }

    // ==================== 转换方法 ====================

    private ForumPostListVO convertToPostListVO(Map<String, Object> map) {
        ForumPostListVO vo = new ForumPostListVO();
        vo.setId(getInteger(map, "id"));
        vo.setUserId(getInteger(map, "userId"));
        vo.setUserName(getString(map, "userName"));
        vo.setUserAvatar(getString(map, "userAvatar"));
        vo.setTitle(getString(map, "title"));
        
        String content = getString(map, "content");
        if (content != null && content.length() > 100) {
            vo.setContent(content.substring(0, 100) + "...");
        } else {
            vo.setContent(content);
        }
        
        vo.setLikeCount(getIntegerDefault(map, "likeCount", 0));
        vo.setCollectCount(getIntegerDefault(map, "collectCount", 0));
        vo.setReplyCount(getIntegerDefault(map, "replyCount", 0));
        vo.setIsTop(getIntegerDefault(map, "isTop", 0));
        vo.setIsEssence(getIntegerDefault(map, "isEssence", 0));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        
        List<String> imageList = parseJsonToList(getString(map, "images"));
        vo.setImages(imageList);
        if (imageList != null && !imageList.isEmpty()) {
            vo.setCoverImage(imageList.get(0));
        }
        
        return vo;
    }

    private ForumPostDetailVO convertToPostDetailVO(Map<String, Object> map) {
        ForumPostDetailVO vo = new ForumPostDetailVO();
        vo.setId(getInteger(map, "id"));
        vo.setUserId(getInteger(map, "userId"));
        vo.setUserName(getString(map, "userName"));
        vo.setUserNickname(getString(map, "userNickname"));
        vo.setUserAvatar(getString(map, "userAvatar"));
        
        String phone = getString(map, "userPhone");
        if (StringUtils.hasText(phone) && phone.length() >= 11) {
            vo.setUserPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        } else {
            vo.setUserPhone(phone);
        }
        
        vo.setTitle(getString(map, "title"));
        vo.setContent(getString(map, "content"));
        vo.setLikeCount(getIntegerDefault(map, "likeCount", 0));
        vo.setCollectCount(getIntegerDefault(map, "collectCount", 0));
        vo.setReplyCount(getIntegerDefault(map, "replyCount", 0));
        vo.setIsTop(getIntegerDefault(map, "isTop", 0));
        vo.setIsEssence(getIntegerDefault(map, "isEssence", 0));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        vo.setImages(parseJsonToList(getString(map, "images")));
        
        return vo;
    }

    private ForumReplyListVO convertToReplyListVO(Map<String, Object> map) {
        ForumReplyListVO vo = new ForumReplyListVO();
        vo.setId(getInteger(map, "id"));
        vo.setPostId(getInteger(map, "postId"));
        vo.setPostTitle(getString(map, "postTitle"));
        vo.setUserId(getInteger(map, "userId"));
        vo.setUserName(getString(map, "userName"));
        vo.setUserAvatar(getString(map, "userAvatar"));
        vo.setParentId(getInteger(map, "parentId"));
        vo.setReplyToUserId(getInteger(map, "replyToUserId"));
        vo.setReplyToUserName(getString(map, "replyToUserName"));
        vo.setContent(getString(map, "content"));
        vo.setLikeCount(getIntegerDefault(map, "likeCount", 0));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        return vo;
    }

    private ForumReplyDetailVO convertToReplyDetailVO(Map<String, Object> map) {
        ForumReplyDetailVO vo = new ForumReplyDetailVO();
        vo.setId(getInteger(map, "id"));
        vo.setPostId(getInteger(map, "postId"));
        vo.setPostTitle(getString(map, "postTitle"));
        vo.setUserId(getInteger(map, "userId"));
        vo.setUserName(getString(map, "userName"));
        vo.setUserAvatar(getString(map, "userAvatar"));
        
        String phone = getString(map, "userPhone");
        if (StringUtils.hasText(phone) && phone.length() >= 11) {
            vo.setUserPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        } else {
            vo.setUserPhone(phone);
        }
        
        vo.setParentId(getInteger(map, "parentId"));
        vo.setParentContent(getString(map, "parentContent"));
        vo.setReplyToUserId(getInteger(map, "replyToUserId"));
        vo.setReplyToUserName(getString(map, "replyToUserName"));
        vo.setContent(getString(map, "content"));
        vo.setLikeCount(getIntegerDefault(map, "likeCount", 0));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        return vo;
    }

    private ForumReportListVO convertToReportListVO(Map<String, Object> map) {
        ForumReportListVO vo = new ForumReportListVO();
        vo.setId(getInteger(map, "id"));
        vo.setAuditNo(getString(map, "auditNo"));
        vo.setAuditType(getInteger(map, "auditType"));
        vo.setAuditTypeName(vo.getAuditType() == 5 ? "帖子举报" : "评论举报");
        vo.setBusinessId(getInteger(map, "businessId"));
        vo.setTitle(getString(map, "title"));
        
        String content = getString(map, "content");
        if (content != null && content.length() > 50) {
            vo.setContent(content.substring(0, 50) + "...");
        } else {
            vo.setContent(content);
        }
        
        String reportedContent = getString(map, "reportedContent");
        if (reportedContent != null && reportedContent.length() > 50) {
            vo.setReportedContent(reportedContent.substring(0, 50) + "...");
        } else {
            vo.setReportedContent(reportedContent);
        }
        
        vo.setCurrentStatus(getInteger(map, "currentStatus"));
        vo.setStatusName(convertStatusName(vo.getCurrentStatus()));
        vo.setPriority(getInteger(map, "priority"));
        vo.setPriorityName(convertPriorityName(vo.getPriority()));
        vo.setApplicantId(getInteger(map, "applicantId"));
        vo.setApplicantName(getString(map, "applicantName"));
        vo.setApplicantAvatar(getString(map, "applicantAvatar"));
        vo.setReportedUserId(getInteger(map, "reportedUserId"));
        vo.setReportedUserName(getString(map, "reportedUserName"));
        vo.setSubmitTime(getLocalDateTime(map, "submitTime"));
        vo.setCurrentAdminName(getString(map, "currentAdminName"));
        return vo;
    }

    private ForumReportDetailVO convertToReportDetailVO(Map<String, Object> map) {
        ForumReportDetailVO vo = new ForumReportDetailVO();
        vo.setId(getInteger(map, "id"));
        vo.setAuditNo(getString(map, "auditNo"));
        vo.setAuditType(getInteger(map, "auditType"));
        vo.setAuditTypeName(vo.getAuditType() == 5 ? "帖子举报" : "评论举报");
        vo.setBusinessId(getInteger(map, "businessId"));
        vo.setTitle(getString(map, "title"));
        vo.setContent(getString(map, "content"));
        vo.setAttachments(getString(map, "attachments"));
        
        if (StringUtils.hasText(vo.getAttachments())) {
            vo.setAttachmentList(Arrays.asList(vo.getAttachments().split(",")));
        } else {
            vo.setAttachmentList(new ArrayList<>());
        }
        
        vo.setCurrentStatus(getInteger(map, "currentStatus"));
        vo.setStatusName(convertStatusName(vo.getCurrentStatus()));
        vo.setPriority(getInteger(map, "priority"));
        vo.setPriorityName(convertPriorityName(vo.getPriority()));
        vo.setSubmitTime(getLocalDateTime(map, "submitTime"));
        vo.setFinishTime(getLocalDateTime(map, "finishTime"));
        vo.setFinalOpinion(getString(map, "finalOpinion"));
        vo.setRejectReason(getString(map, "rejectReason"));
        vo.setApplicantId(getInteger(map, "applicantId"));
        vo.setApplicantUsername(getString(map, "applicantUsername"));
        vo.setApplicantName(getString(map, "applicantName"));
        vo.setApplicantAvatar(getString(map, "applicantAvatar"));
        
        String phone = getString(map, "applicantPhone");
        if (StringUtils.hasText(phone) && phone.length() >= 11) {
            vo.setApplicantPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        } else {
            vo.setApplicantPhone(phone);
        }
        
        vo.setCurrentAdminId(getInteger(map, "currentAdminId"));
        vo.setCurrentAdminName(getString(map, "currentAdminName"));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        return vo;
    }

    private AuditLogVO convertToAuditLogVO(Map<String, Object> map) {
        AuditLogVO vo = new AuditLogVO();
        vo.setId(getInteger(map, "id"));
        vo.setAction(getInteger(map, "action"));
        vo.setActionName(convertActionName(vo.getAction()));
        vo.setFromStatus(getInteger(map, "fromStatus"));
        vo.setFromStatusName(convertStatusName(vo.getFromStatus()));
        vo.setToStatus(getInteger(map, "toStatus"));
        vo.setToStatusName(convertStatusName(vo.getToStatus()));
        vo.setOpinion(getString(map, "opinion"));
        vo.setAdminId(getInteger(map, "adminId"));
        vo.setAdminName(getString(map, "adminName"));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        return vo;
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

    // ==================== 工具方法 ====================

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

    private List<String> parseJsonToList(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("解析图片 JSON 失败: {}", json, e);
            return new ArrayList<>();
        }
    }
}
