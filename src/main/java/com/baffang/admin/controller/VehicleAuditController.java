package com.baffang.admin.controller;

import com.baffang.admin.dto.VehicleAuditQueryDTO;
import com.baffang.admin.dto.VehicleAuditReviewDTO;
import com.baffang.admin.service.VehicleAuditService;
import com.baffang.admin.vo.VehicleAuditDetailVO;
import com.baffang.admin.vo.VehicleAuditListVO;
import com.baffang.admin.vo.VehicleAuditStatisticsVO;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.github.pagehelper.Page;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/audit/vehicle")
public class VehicleAuditController {

    @Autowired
    private VehicleAuditService vehicleAuditService;

    /**
     * 查询车主认证审核列表
     */
    @GetMapping("/list")
    @SystemLog(module = "车主认证审核", action = "查询审核列表", saveResult = false)
    public Result<PageResult> getAuditList(
            VehicleAuditQueryDTO query,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2); // 审核员及以上
        
        Page<VehicleAuditListVO> pageData = vehicleAuditService.getAuditList(query);
        return PageResult.quicklyReturn(pageData);
    }

    /**
     * 查询车主认证审核详情
     */
    @GetMapping("/detail/{id}")
    @SystemLog(module = "车主认证审核", action = "查询审核详情")
    public Result<VehicleAuditDetailVO> getAuditDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        VehicleAuditDetailVO detail = vehicleAuditService.getAuditDetail(id);
        return Result.success(detail);
    }

    /**
     * 认领审核任务
     */
    @PostMapping("/claim/{id}")
    @SystemLog(module = "车主认证审核", action = "认领审核任务")
    public Result<Void> claimAudit(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 2);
        
        vehicleAuditService.claimAudit(id, adminId);
        return Result.success();
    }

    /**
     * 提交审核结果
     */
    @PostMapping("/review")
    @SystemLog(module = "车主认证审核", action = "提交审核结果")
    public Result<Void> submitReview(
            @Valid @RequestBody VehicleAuditReviewDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 2);
        
        vehicleAuditService.submitReview(dto, adminId);
        return Result.success();
    }


    /**
     * 获取审核统计
     */
    @GetMapping("/statistics")
    public Result<VehicleAuditStatisticsVO> getStatistics(HttpServletRequest request) {
        checkAdminPermission(request, 2);
        
        VehicleAuditStatisticsVO statistics = vehicleAuditService.getStatistics();
        return Result.success(statistics);
    }

   /**
 * 检查管理员权限并返回管理员ID（复用私桩审核的兼容逻辑）
 */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
