package com.baffang.admin.controller;

import com.baffang.admin.service.StatsService;
import com.baffang.admin.vo.PlatformStatsVO;
import com.baffang.admin.vo.StationDistributionVO;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台统计控制器
 */
@RestController
@RequestMapping("/api/v1/admin/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /**
     * 获取平台统计数据
     * 权限：普通管理员及以上（type <= 1）
     */
    @GetMapping("/platform")
    public Result<PlatformStatsVO> getPlatformStats(HttpServletRequest request) {
        // 权限校验：只有普通管理员及以上可访问
        checkAdminPermission(request, 1);
        
        PlatformStatsVO stats = statsService.getPlatformStats();
        return Result.success(stats);
    }


     /**
     * 获取私桩分布数据
     * 权限：普通管理员及以上（type <= 1）
     */
    @GetMapping("/station/distribution")
    public Result<StationDistributionVO> getStationDistribution(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        StationDistributionVO distribution = statsService.getStationDistribution();
        return Result.success(distribution);
    }

    /**
 * 校验管理员权限
 * @param request 请求
 * @param maxType 允许的最大 type 值（type 越小权限越大）
 */
    private void checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        adminGuard.requireAdmin(maxType);
    }

    @Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;




}
