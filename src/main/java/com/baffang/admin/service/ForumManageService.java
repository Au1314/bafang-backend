package com.baffang.admin.service;

import com.baffang.admin.dto.*;
import com.baffang.admin.vo.*;
import com.github.pagehelper.Page;

import java.util.List;

public interface ForumManageService {

    // ==================== 帖子管理 ====================
    
    /**
     * 获取帖子列表
     */
    Page<ForumPostListVO> getPostList(ForumPostQueryDTO queryDTO);

    /**
     * 获取帖子详情
     */
    ForumPostDetailVO getPostDetail(Integer id);

    /**
     * 删除帖子
     */
    void deletePost(Integer id, Integer adminId);

    /**
     * 批量删除帖子
     */
    void batchDeletePost(List<Integer> ids, Integer adminId);

    /**
     * 设置置顶
     */
    void setTop(Integer id, Integer isTop, Integer adminId);

    /**
     * 设置精华
     */
    void setEssence(Integer id, Integer isEssence, Integer adminId);

    // ==================== 评论管理 ====================

    /**
     * 获取评论列表
     */
    Page<ForumReplyListVO> getReplyList(ForumReplyQueryDTO queryDTO);

    /**
     * 获取评论详情
     */
    ForumReplyDetailVO getReplyDetail(Integer id);

    /**
     * 删除评论
     */
    void deleteReply(Integer id, Integer adminId);

    /**
     * 批量删除评论
     */
    void batchDeleteReply(List<Integer> ids, Integer adminId);

    // ==================== 举报审核 ====================

    /**
     * 获取帖子举报列表
     */
    Page<ForumReportListVO> getPostReportList(ForumReportQueryDTO queryDTO);

    /**
     * 获取评论举报列表
     */
    Page<ForumReportListVO> getReplyReportList(ForumReportQueryDTO queryDTO);

    /**
     * 获取举报详情
     */
    ForumReportDetailVO getReportDetail(Integer id);

    /**
     * 审核通过
     */
    void approveReport(Integer id, ForumReportAuditDTO dto, Integer adminId);

    /**
     * 审核拒绝
     */
    void rejectReport(Integer id, ForumReportAuditDTO dto, Integer adminId);

    // ==================== 统计 ====================

    /**
     * 获取论坛统计数据
     */
    ForumStatisticsVO getStatistics();
}
