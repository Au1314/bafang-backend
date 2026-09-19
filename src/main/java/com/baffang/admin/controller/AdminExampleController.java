package com.baffang.admin.controller;

import com.baffang.common.constant.PermissionConst;
import com.baffang.common.result.Result;
import com.baffang.framework.permission.annotation.RequirePermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员系统权限示例控制器
 * 展示如何正确使用@RequirePermission注解与PermissionConst常量
 */
@RestController
@RequestMapping("/api/v1/admin/example")
public class AdminExampleController {

    /**
     * 整个类的接口都需要管理员权限
     * 这里使用了最基础的管理员权限
     */
    @RequirePermission(PermissionConst.ADMIN_COMMON_ADMIN)
    @GetMapping("/dashboard")
    public Result<String> getAdminDashboard() {
        return Result.success("获取管理员仪表盘信息成功");
    }

    /**
     * 普通管理员接口
     */
    @RequirePermission(PermissionConst.ADMIN_COMMON_ADMIN)
    @GetMapping("/user/list")
    public Result<String> getUserList() {
        return Result.success("获取用户列表成功");
    }

    /**
     * 超级管理员接口
     */
    @RequirePermission(PermissionConst.ADMIN_SUPER_ADMIN)
    @GetMapping("/system/config")
    public Result<String> getSystemConfig() {
        return Result.success("获取系统配置成功");
    }

    /**
     * 超级管理员才能执行的操作
     */
    @RequirePermission(PermissionConst.ADMIN_SUPER_ADMIN)
    @PostMapping("/system/config")
    public Result<String> updateSystemConfig() {
        return Result.success("更新系统配置成功");
    }

    /**
     * 客户服务接口
     */
    @RequirePermission(PermissionConst.ADMIN_CUSTOMER_SERVICE)
    @GetMapping("/user/complaints")
    public Result<String> getCustomerComplaints() {
        return Result.success("获取用户投诉信息成功");
    }

    /**
     * 审核员接口
     */
    @RequirePermission(PermissionConst.ADMIN_AUDITOR)
    @GetMapping("/user/audit")
    public Result<String> getAuditList() {
        return Result.success("获取审核列表成功");
    }

    /**
     * 论坛管理员接口
     */
    @RequirePermission(PermissionConst.ADMIN_FORUM_ADMIN)
    @GetMapping("/forum/posts")
    public Result<String> getForumPosts() {
        return Result.success("获取论坛帖子列表成功");
    }

    /**
     * 紧急响应人员接口
     */
    @RequirePermission(PermissionConst.ADMIN_EMERGENCY_RESPONDER)
    @GetMapping("/emergency/alerts")
    public Result<String> getEmergencyAlerts() {
        return Result.success("获取紧急警报信息成功");
    }

    /**
     * 只需要登录即可访问，不需要特定权限
     */
    @RequirePermission(needPermission = false)
    @GetMapping("/profile")
    public Result<String> getAdminProfile() {
        return Result.success("获取管理员信息成功");
    }

    /**
     * 不需要登录即可访问的接口
     */
    @RequirePermission(needLogin = false, needPermission = false)
    @GetMapping("/public/notice")
    public Result<String> getPublicNotice() {
        return Result.success("获取公开公告成功");
    }
}
