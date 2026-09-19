package com.baffang.admin.controller;

import com.baffang.admin.dto.StationAuthReviewDTO;
import com.baffang.admin.service.StationAuthService;
import com.baffang.admin.vo.StationAuthDetailVO;
import com.baffang.admin.vo.StationAuthListVO;
import com.baffang.common.annotation.SystemLog;  // ← 添加导入
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.github.pagehelper.Page;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth/station")
public class StationAuthController {

    @Autowired
    private StationAuthService stationAuthService;

    /**
     * 获取私桩认证列表
     */
    @GetMapping("/list")
    @SystemLog(module = "私桩认证审核", action = "获取认证列表", saveResult = false)
    public Result<PageResult> getAuthList(
            @RequestParam(required = false) Integer authStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        Page<StationAuthListVO> pageData = stationAuthService.getAuthList(authStatus, page, size);
        
        return PageResult.quicklyReturn(pageData);
    }

    /**
     * 获取私桩认证详情
     */
    @GetMapping("/detail/{stationId}")
    @SystemLog(module = "私桩认证审核", action = "获取认证详情")
    public Result<StationAuthDetailVO> getAuthDetail(
            @PathVariable Integer stationId,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        StationAuthDetailVO detail = stationAuthService.getAuthDetail(stationId);
        return Result.success(detail);
    }

    /**
     * 审核私桩认证
     */
    @PostMapping("/review")
    @SystemLog(module = "私桩认证审核", action = "提交审核结果")
    public Result<Void> reviewAuth(
            @RequestBody StationAuthReviewDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 2);
        
        stationAuthService.reviewAuth(dto, adminId);
        return Result.success(null);
    }

    /**
     * 检查管理员权限并返回管理员ID
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
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
            
            // 获取管理员类型
            Integer adminType = null;
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
            
            if (adminType == null || adminType > maxType) {
                throw new BusinessException(ErrorMsg.NO_PERMISSION);
            }
            
            // 获取管理员ID
            Object adminIdObj = claims.get("adminId");
            if (adminIdObj instanceof Number) {
                return ((Number) adminIdObj).intValue();
            }
            
            // 兼容旧格式
            Object userIdObj = claims.get(JwtConstants.USER_ID_CLAIM);
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).intValue();
            }
            
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        }
    }
}
