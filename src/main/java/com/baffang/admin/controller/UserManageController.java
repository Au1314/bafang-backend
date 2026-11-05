package com.baffang.admin.controller;

import com.baffang.admin.dto.BatchDeleteDTO;
import com.baffang.admin.dto.UserQueryDTO;
import com.baffang.admin.dto.UserStatusDTO;
import com.baffang.admin.dto.UserUpdateDTO;
import com.baffang.admin.service.UserManageService;
import com.baffang.admin.vo.UserDetailVO;
import com.baffang.admin.vo.UserListVO;
import com.baffang.admin.vo.UserStatisticsVO;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user")
@Tag(name = "用户信息管理")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;

    /**
     * 获取用户列表
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    @SystemLog(module = "用户管理", action = "获取用户列表", saveResult = false)
    public Result<PageResult> getUserList(
            UserQueryDTO query,
            HttpServletRequest request) {
        
        // 超管(0)和普管(1)可访问，maxType=1
        checkAdminPermission(request, 1);
        
        Page<UserListVO> page = userManageService.getUserList(query);
        return PageResult.quicklyReturn(page);
    }

    /**
     * 获取用户详情
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取用户详情")
    @SystemLog(module = "用户管理", action = "获取用户详情")
    public Result<UserDetailVO> getUserDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 1);
        return Result.success(userManageService.getUserDetail(id));
    }

    /**
     * 编辑用户信息
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @PutMapping("/{id}")
    @Operation(summary = "编辑用户信息")
    @SystemLog(module = "用户管理", action = "编辑用户信息")
    public Result<?> updateUser(
            @PathVariable Integer id,
            @RequestBody @Validated UserUpdateDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 1);
        log.info("管理员[{}]编辑用户[{}]信息", adminId, id);
        userManageService.updateUser(id, dto);
        return Result.success();
    }

    /**
     * 修改用户状态（禁用/启用）
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改用户状态（禁用/启用）")
    @SystemLog(module = "用户管理", action = "修改用户状态")
    public Result<?> updateUserStatus(
            @PathVariable Integer id,
            @RequestBody @Validated UserStatusDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 1);
        String action = dto.getStatus() == 1 ? "禁用" : "启用";
        log.info("管理员[{}]{}用户[{}]，原因：{}", adminId, action, id, dto.getReason());
        userManageService.updateUserStatus(id, dto);
        return Result.success();
    }

    /**
     * 重置用户密码
     * 权限：仅超级管理员(0)
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "重置用户密码")
    @SystemLog(module = "用户管理", action = "重置用户密码")
    public Result<String> resetPassword(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        // 仅超管可操作，maxType=0
        Integer adminId = checkAdminPermission(request, 0);
        log.info("管理员[{}]重置用户[{}]密码", adminId, id);
        String newPassword = userManageService.resetPassword(id);
        return Result.success(newPassword);
    }

    /**
     * 删除用户
     * 权限：仅超级管理员(0)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @SystemLog(module = "用户管理", action = "删除用户")
    public Result<?> deleteUser(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 0);
        log.info("管理员[{}]删除用户[{}]", adminId, id);
        userManageService.deleteUser(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     * 权限：仅超级管理员(0)
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户")
    @SystemLog(module = "用户管理", action = "批量删除用户")
    public Result<?> batchDeleteUsers(
            @RequestBody @Validated BatchDeleteDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 0);
        log.info("管理员[{}]批量删除用户：{}", adminId, dto.getIds());
        userManageService.batchDeleteUsers(dto.getIds());
        return Result.success();
    }

    /**
     * 获取用户统计
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取用户统计")
    public Result<UserStatisticsVO> getStatistics(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        return Result.success(userManageService.getStatistics());
    }

    // ==================== 复用：JWT权限校验方法 ====================
    
    /**
     * 检查管理员权限并返回管理员ID
     * @param request HTTP请求
     * @param maxType 允许的最大管理员类型（0-超管，1-普管，2-审核员...）
     * @return 管理员ID
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
