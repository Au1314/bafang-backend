package com.baffang.admin.controller;

import com.baffang.admin.dto.*;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.constant.NoticeConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 管理端 - 通知控制器
 */
@Slf4j
@RestController
@Tag(name = "管理端-通知管理", description = "管理员通知相关接口")
public class AdminNoticeController {

    @Autowired
    private AdminNoticeService noticeService;

    /**
     * 查询所有通知（分页）
     */
    @GetMapping(NoticeConstants.ADMIN_NOTICE_LIST)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "查询通知列表", description = "查询所有通知，支持分页和筛选")
    public Result<AdminNoticePageDTO> getNoticeList(
            @Parameter(description = "通知类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "接收用户ID") @RequestParam(required = false) Integer userId,
            @Parameter(description = "是否已读") @RequestParam(required = false) Integer isRead,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize) {

        AdminNoticeQueryDTO query = new AdminNoticeQueryDTO();
        query.setType(type);
        query.setUserId(userId);
        query.setIsRead(isRead);
        query.setKeyword(keyword);
        query.setPage(page);
        query.setPageSize(pageSize);

        AdminNoticePageDTO result = noticeService.getNoticeList(query);
        return Result.success(result);
    }

    /**
     * 获取通知详情
     */
    @GetMapping(NoticeConstants.ADMIN_NOTICE_DETAIL)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "获取通知详情", description = "根据ID获取通知详情")
    public Result<AdminNoticeDTO> getNoticeDetail(
            @Parameter(description = "通知ID") @PathVariable Integer id) {

        AdminNoticeDTO notice = noticeService.getNoticeDetail(id);
        return Result.success(notice);
    }

    /**
     * 发送系统公告（全体用户）
     */
    @PostMapping(NoticeConstants.ADMIN_NOTICE_BROADCAST)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "发送系统公告", description = "发送公告给全体用户")
    public Result<Void> broadcast(
            @RequestBody @Validated BroadcastNoticeDTO dto,
            HttpServletRequest request) {

        Integer adminId = getAdminIdFromRequest(request);
        noticeService.broadcast(dto);
        log.info("管理员 {} 发送系统公告: {}", adminId, dto.getTitle());
        return Result.success(null);
    }

    /**
     * 发送通知（可指定用户）
     */
    @PostMapping(NoticeConstants.ADMIN_NOTICE_SEND)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "发送通知", description = "发送通知给指定用户或全体用户")
    public Result<Void> sendNotice(
            @RequestBody @Validated SendNoticeDTO dto,
            HttpServletRequest request) {

        Integer adminId = getAdminIdFromRequest(request);
        noticeService.sendNotice(dto);
        log.info("管理员 {} 发送通知: {}, 目标用户: {}",
                adminId, dto.getTitle(), dto.getUserId() == null ? "全体" : dto.getUserId());
        return Result.success(null);
    }

    /**
     * 批量发送通知
     */
    @PostMapping(NoticeConstants.ADMIN_NOTICE_BATCH_SEND)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "批量发送通知", description = "发送通知给多个指定用户")
    public Result<Void> batchSendNotice(
            @RequestBody @Validated BatchSendNoticeDTO dto,
            HttpServletRequest request) {

        Integer adminId = getAdminIdFromRequest(request);
        noticeService.batchSendNotice(dto);
        log.info("管理员 {} 批量发送通知: {}, 目标用户数: {}",
                adminId, dto.getTitle(), dto.getUserIds().size());
        return Result.success(null);
    }

    /**
     * 删除通知
     */
    @DeleteMapping(NoticeConstants.ADMIN_NOTICE_DELETE)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "删除通知", description = "删除指定通知")
    public Result<Void> deleteNotice(
            @Parameter(description = "通知ID") @PathVariable Integer id,
            HttpServletRequest request) {

        Integer adminId = getAdminIdFromRequest(request);
        boolean success = noticeService.deleteNotice(id);
        if (success) {
            log.info("管理员 {} 删除通知: {}", adminId, id);
            return Result.success(null);
        }
        return Result.error("删除失败，通知不存在");
    }

    /**
     * 批量删除通知
     */
    @DeleteMapping(NoticeConstants.ADMIN_NOTICE_BATCH_DELETE)
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    @Operation(summary = "批量删除通知", description = "批量删除多条通知")
    public Result<Integer> batchDeleteNotice(
            @RequestBody @Validated BatchDeleteNoticeDTO dto,
            HttpServletRequest request) {

        Integer adminId = getAdminIdFromRequest(request);
        int count = noticeService.batchDeleteNotice(dto.getIds());
        log.info("管理员 {} 批量删除通知: {} 条", adminId, count);
        return Result.success(count);
    }

    /**
     * 从请求中获取管理员ID
     */
    private Integer getAdminIdFromRequest(HttpServletRequest request) {
        // 安全整改：统一委托 AdminGuard——按用户名查库确认管理员身份，防用户 token 借 adminId/userId 撞号越权
        return adminGuard.requireAdmin(com.baffang.framework.permission.AdminGuard.ANY_ADMIN);
    }

    @Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
