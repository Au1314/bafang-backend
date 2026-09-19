package com.baffang.admin.controller;

import com.baffang.admin.dto.SystemLogQueryDTO;
import com.baffang.admin.service.SystemLogService;
import com.baffang.admin.vo.SystemLogDetailVO;
import com.baffang.admin.vo.SystemLogListVO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/system/log")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    /**
     * 查询系统日志列表
     */
    @GetMapping("/list")
    @SystemLog(module = "系统日志", action = "查询日志列表", saveResult = false)
    public Result<PageResult> getLogList(
            SystemLogQueryDTO query,
            HttpServletRequest request) {
        
        // 只有超级管理员可以查看系统日志
        checkAdminPermission(request, 1);
        
        Page<SystemLogListVO> pageData = systemLogService.getLogList(query);
        return PageResult.quicklyReturn(pageData);
    }

    /**
     * 查询系统日志详情
     */
    @GetMapping("/detail/{id}")
    @SystemLog(module = "系统日志", action = "查询日志详情")
    public Result<SystemLogDetailVO> getLogDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 1);
        
        SystemLogDetailVO detail = systemLogService.getLogDetail(id);
        return Result.success(detail);
    }

    /**
     * 获取所有模块名称（用于筛选下拉框）
     */
    @GetMapping("/modules")
    public Result<List<String>> getModules(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        
        List<String> modules = systemLogService.getModules();
        return Result.success(modules);
    }

    /**
     * 检查管理员权限
     */
    private void checkAdminPermission(HttpServletRequest request, int maxType) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorMsg.USER_NOT_LOGIN);
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = JwtUtil.parseToken(JwtConstants.SECRET_KEY, token);

            String authType = claims.get(JwtConstants.AUTH_TYPE_CLAIM, String.class);
            if (!"admin".equals(authType)) {
                throw new BusinessException(ErrorMsg.NO_PERMISSION);
            }

            Integer adminType = null;
            Object typeObj = claims.get("type");
            if (typeObj instanceof Number) {
                adminType = ((Number) typeObj).intValue();
            }

            if (adminType == null) {
                Object roleObj = claims.get(JwtConstants.ROLE_CLAIM);
                if (roleObj instanceof Number) {
                    adminType = ((Number) roleObj).intValue();
                }
            }

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
