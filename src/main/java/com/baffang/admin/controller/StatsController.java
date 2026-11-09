package com.baffang.admin.controller;

import com.baffang.admin.service.StatsService;
import com.baffang.admin.vo.PlatformStatsVO;
import com.baffang.admin.vo.ScreenStatsVO;
import com.baffang.admin.vo.StationDistributionVO;
import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
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

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

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
     * 数据大屏汇总统计（真实数据）
     * 权限：普通管理员及以上（type <= 1）
     */
    @GetMapping("/screen")
    @Operation(summary = "数据大屏汇总统计")
    public Result<ScreenStatsVO> getScreenStats(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        return Result.success(statsService.getScreenStats());
    }

    /**
     * 在线人数统计（契约补齐：Dashboard 在线用户数真实来源）
     * 口径：WS 客服通道当前在线的管理员数与用户数
     */
    @GetMapping("/online")
    @Operation(summary = "在线人数统计")
    public Result<java.util.Map<String, Integer>> getOnlineStats(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("onlineAdmins", webSocketSessionManager.getOnlineAdminCount());
        stats.put("onlineUsers", webSocketSessionManager.getOnlineUserCount());
        return Result.success(stats);
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
