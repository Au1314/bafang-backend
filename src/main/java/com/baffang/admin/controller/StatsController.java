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
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new BusinessException(ErrorMsg.USER_NOT_LOGIN);
    }
    
    String token = authHeader.substring(7).trim();
    
    try {
        Claims claims = JwtUtil.parseToken(JwtConstants.SECRET_KEY, token);
        
        // 验证是否为管理员 Token
        String authType = claims.get(JwtConstants.AUTH_TYPE_CLAIM, String.class);
        if (!"admin".equals(authType)) {
            throw new BusinessException(ErrorMsg.NO_PERMISSION);
        }
        
        // 获取管理员类型 - 登录时存的是 "type"，不是 ROLE_CLAIM
        Integer adminType = null;
        
        // 优先读取 "type"
        Object typeObj = claims.get("type");
        if (typeObj instanceof Number) {
            adminType = ((Number) typeObj).intValue();
        }
        
        // 兼容 ROLE_CLAIM
        if (adminType == null) {
            Object roleObj = claims.get(JwtConstants.ROLE_CLAIM);
            if (roleObj instanceof Number) {
                adminType = ((Number) roleObj).intValue();
            }
        }
        
        // 检查权限
        if (adminType == null || adminType > maxType) {
            throw new BusinessException(ErrorMsg.NO_PERMISSION);
        }
        
    } catch (BusinessException e) {
        throw e;
    } catch (Exception e) {
        throw new BusinessException(ErrorMsg.TOKEN_INVALID);
    }
}




}
