package com.baffang.admin.controller;

import com.baffang.admin.dto.*;
import com.baffang.admin.service.CustomerServiceService;
import com.baffang.admin.vo.*;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/customer-service")
@RequiredArgsConstructor
@Tag(name = "客服管理")
public class CustomerServiceController {
    
    private final CustomerServiceService customerServiceService;
    
    // ==================== AI配置管理 ====================
    
    @GetMapping("/ai-config")
    @Operation(summary = "获取AI客服配置")
    @SystemLog(module = "客服管理", action = "获取AI客服配置", saveResult = false)
    public Result<AiConfigVO> getAiConfig(HttpServletRequest request) {
        System.out.println("======== 进入 getAiConfig 方法 ========");
        // 超管/管理员可访问（maxType=1：0-超管，1-普管）
        checkAdminPermission(request, 1);
        return Result.success(customerServiceService.getAiConfig());
    }
    
    @PutMapping("/ai-config")
    @Operation(summary = "更新AI客服配置")
    @SystemLog(module = "客服管理", action = "更新AI客服配置")
    public Result<Void> updateAiConfig(@RequestBody AiConfigDTO dto, HttpServletRequest request) {
        // 仅超管可操作（maxType=0）
        checkAdminPermission(request, 0);
        customerServiceService.updateAiConfig(dto);
        return Result.success();
    }
    
    // ==================== 人工客服对话 ====================
    
    @GetMapping("/pending")
    @Operation(summary = "获取待接入对话列表")
    @SystemLog(module = "客服管理", action = "获取待接入对话列表", saveResult = false)
    public Result<PageInfo<PendingConversationVO>> getPendingConversations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        // 客服/管理员/超管可访问（maxType=2：2-客服，1-普管，0-超管）
        checkAdminPermission(request, 3);
        return Result.success(customerServiceService.getPendingConversations(pageNum, pageSize));
    }
    
    @PostMapping("/conversations/{id}/join")
    @Operation(summary = "接入对话")
    @SystemLog(module = "客服管理", action = "接入对话")
    public Result<Void> joinConversation(
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        customerServiceService.joinConversation(conversationId, adminId);
        return Result.success();
    }
    
    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "发送消息")
    @SystemLog(module = "客服管理", action = "发送客服消息")
    public Result<MessageVO> sendMessage(
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            @Validated @RequestBody SendMessageDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        return Result.success(customerServiceService.sendMessage(conversationId, dto, adminId));
    }
    
    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "获取对话消息列表")
    @SystemLog(module = "客服管理", action = "获取对话消息列表", saveResult = false)
    public Result<PageInfo<MessageVO>> getMessages(
        
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "50") Integer pageSize,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        return Result.success(customerServiceService.getMessages(conversationId, pageNum, pageSize, adminId));
    }
    
    @PostMapping("/conversations/{id}/close")
    @Operation(summary = "结束对话")
    @SystemLog(module = "客服管理", action = "结束客服对话")
    public Result<Void> closeConversation(
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        customerServiceService.closeConversation(conversationId, adminId);
        return Result.success();
    }
    
    @PostMapping("/conversations/{id}/transfer")
    @Operation(summary = "转接对话")
    @SystemLog(module = "客服管理", action = "转接客服对话")
    public Result<Void> transferConversation(
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            @Validated @RequestBody TransferConversationDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 3);
        customerServiceService.transferConversation(conversationId, dto, adminId);
        return Result.success();
    }
    @GetMapping("/my-conversations")
@Operation(summary = "获取我的对话列表")
@SystemLog(module = "客服管理", action = "获取我的对话列表", saveResult = false)
public Result<PageInfo<ConversationListVO>> getMyConversations(
        @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
        @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
        @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status,
        HttpServletRequest request) {
    Integer adminId = checkAdminPermission(request, 3);
    return Result.success(customerServiceService.getMyConversations(pageNum, pageSize, status, adminId));
}

    // ==================== 对话记录管理 ====================
    
    @GetMapping("/conversations")
    @Operation(summary = "分页查询对话列表")
    @SystemLog(module = "客服管理", action = "分页查询对话列表", saveResult = false)
    public Result<PageInfo<ConversationListVO>> getConversationList(
            ConversationQueryDTO queryDTO,
            HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(customerServiceService.getConversationList(queryDTO));
    }
    
    @GetMapping("/conversations/{id}")
    @Operation(summary = "获取对话详情")
    @SystemLog(module = "客服管理", action = "获取对话详情", saveResult = false)
    public Result<ConversationDetailVO> getConversationDetail(
            @Parameter(description = "对话ID") @PathVariable("id") Integer conversationId,
            HttpServletRequest request) {
        checkAdminPermission(request, 3);
        return Result.success(customerServiceService.getConversationDetail(conversationId));
    }

    // ==================== JWT权限校验方法 ====================

    /**
     * 校验管理员权限
     * @param request HttpServletRequest
     * @param maxType 最大允许的管理员类型（0-超管，1-普通管理员，2-客服人员）
     * @return 当前管理员ID
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;


    // ==================== 客服统计 ====================

@GetMapping("/stats/overview")
@Operation(summary = "获取客服统计概览")
@SystemLog(module = "客服管理", action = "获取客服统计概览", saveResult = false)
public Result<CustomerServiceStatsVO> getStatsOverview(HttpServletRequest request) {
    Integer adminId = checkAdminPermission(request, 3);
    return Result.success(customerServiceService.getStatsOverview(adminId));
}

@GetMapping("/stats/my")
@Operation(summary = "获取我的统计数据")
@SystemLog(module = "客服管理", action = "获取我的统计数据", saveResult = false)
public Result<AdminStatsVO> getMyStats(HttpServletRequest request) {
    Integer adminId = checkAdminPermission(request, 3);
    return Result.success(customerServiceService.getMyStats(adminId));
}

@GetMapping("/stats/admins")
@Operation(summary = "获取所有客服统计(管理员用)")
@SystemLog(module = "客服管理", action = "获取所有客服统计", saveResult = false)
public Result<List<AdminStatsVO>> getAllAdminStats(HttpServletRequest request) {
    // 仅管理员可查看所有客服统计
    checkAdminPermission(request, 1);
    return Result.success(customerServiceService.getAllAdminStats());
}

}