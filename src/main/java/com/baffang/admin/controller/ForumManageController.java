package com.baffang.admin.controller;

import com.baffang.admin.dto.*;
import com.baffang.admin.service.ForumManageService;
import com.baffang.admin.vo.*;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.github.pagehelper.Page;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/forum")
@Tag(name = "论坛管理")
@RequiredArgsConstructor
public class ForumManageController {

    private final ForumManageService forumManageService;

    // ==================== 帖子管理 ====================

    @GetMapping("/post/list")
    @Operation(summary = "获取帖子列表")
    @SystemLog(module = "论坛管理", action = "获取帖子列表", saveResult = false)
    public Result<PageResult> getPostList(ForumPostQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 3); // 论坛管理员及以上
        Page<ForumPostListVO> page = forumManageService.getPostList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/post/{id}")
    @Operation(summary = "获取帖子详情")
    @SystemLog(module = "论坛管理", action = "获取帖子详情")
    public Result<ForumPostDetailVO> getPostDetail(
            @Parameter(description = "帖子ID") @PathVariable Integer id,
            HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(forumManageService.getPostDetail(id));
    }

    @DeleteMapping("/post/{id}")
    @Operation(summary = "删除帖子")
    @SystemLog(module = "论坛管理", action = "删除帖子")
    public Result<?> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Integer id,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.deletePost(id, adminId);
        return Result.success();
    }

    @DeleteMapping("/post/batch")
    @Operation(summary = "批量删除帖子")
    @SystemLog(module = "论坛管理", action = "批量删除帖子")
    public Result<?> batchDeletePost(
            @RequestBody @Validated BatchDeleteDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 1); // 普通管理员及以上
        forumManageService.batchDeletePost(dto.getIds(), adminId);
        return Result.success();
    }

    @PutMapping("/post/{id}/top")
    @Operation(summary = "设置/取消置顶")
    @SystemLog(module = "论坛管理", action = "设置置顶")
    public Result<?> setTop(
            @Parameter(description = "帖子ID") @PathVariable Integer id,
            @RequestBody @Validated PostTopDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.setTop(id, dto.getIsTop(), adminId);
        return Result.success();
    }

    @PutMapping("/post/{id}/essence")
    @Operation(summary = "设置/取消精华")
    @SystemLog(module = "论坛管理", action = "设置精华")
    public Result<?> setEssence(
            @Parameter(description = "帖子ID") @PathVariable Integer id,
            @RequestBody @Validated PostEssenceDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.setEssence(id, dto.getIsEssence(), adminId);
        return Result.success();
    }

    // ==================== 评论管理 ====================

    @GetMapping("/reply/list")
    @Operation(summary = "获取评论列表")
    @SystemLog(module = "论坛管理", action = "获取评论列表", saveResult = false)
    public Result<PageResult> getReplyList(ForumReplyQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 3);
        Page<ForumReplyListVO> page = forumManageService.getReplyList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/reply/{id}")
    @Operation(summary = "获取评论详情")
    @SystemLog(module = "论坛管理", action = "获取评论详情")
    public Result<ForumReplyDetailVO> getReplyDetail(
            @Parameter(description = "评论ID") @PathVariable Integer id,
            HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(forumManageService.getReplyDetail(id));
    }

    @DeleteMapping("/reply/{id}")
    @Operation(summary = "删除评论")
    @SystemLog(module = "论坛管理", action = "删除评论")
    public Result<?> deleteReply(
            @Parameter(description = "评论ID") @PathVariable Integer id,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.deleteReply(id, adminId);
        return Result.success();
    }

    @DeleteMapping("/reply/batch")
    @Operation(summary = "批量删除评论")
    @SystemLog(module = "论坛管理", action = "批量删除评论")
    public Result<?> batchDeleteReply(
            @RequestBody @Validated BatchDeleteDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 1);
        forumManageService.batchDeleteReply(dto.getIds(), adminId);
        return Result.success();
    }

    // ==================== 举报审核 ====================

    @GetMapping("/report/post/list")
    @Operation(summary = "获取帖子举报列表")
    @SystemLog(module = "论坛管理", action = "获取帖子举报列表", saveResult = false)
    public Result<PageResult> getPostReportList(ForumReportQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 3);
        Page<ForumReportListVO> page = forumManageService.getPostReportList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/report/reply/list")
    @Operation(summary = "获取评论举报列表")
    @SystemLog(module = "论坛管理", action = "获取评论举报列表", saveResult = false)
    public Result<PageResult> getReplyReportList(ForumReportQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 3);
        Page<ForumReportListVO> page = forumManageService.getReplyReportList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/report/{id}")
    @Operation(summary = "获取举报详情")
    @SystemLog(module = "论坛管理", action = "获取举报详情")
    public Result<ForumReportDetailVO> getReportDetail(
            @Parameter(description = "举报记录ID") @PathVariable Integer id,
            HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(forumManageService.getReportDetail(id));
    }

    @PostMapping("/report/{id}/approve")
    @Operation(summary = "举报审核通过")
    @SystemLog(module = "论坛管理", action = "举报审核通过")
    public Result<?> approveReport(
            @Parameter(description = "举报记录ID") @PathVariable Integer id,
            @RequestBody @Validated ForumReportAuditDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.approveReport(id, dto, adminId);
        return Result.success();
    }

    @PostMapping("/report/{id}/reject")
    @Operation(summary = "举报审核拒绝")
    @SystemLog(module = "论坛管理", action = "举报审核拒绝")
    public Result<?> rejectReport(
            @Parameter(description = "举报记录ID") @PathVariable Integer id,
            @RequestBody @Validated ForumReportAuditDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        forumManageService.rejectReport(id, dto, adminId);
        return Result.success();
    }

    // ==================== 统计 ====================

    @GetMapping("/statistics")
    @Operation(summary = "获取论坛统计数据")
    public Result<ForumStatisticsVO> getStatistics(HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(forumManageService.getStatistics());
    }

    // ==================== JWT权限校验方法 ====================

    /**
     * 校验管理员权限
     * @param maxType 最大允许的管理员类型（0-超管，1-普管，2-审核员，3-论坛管理员）
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
