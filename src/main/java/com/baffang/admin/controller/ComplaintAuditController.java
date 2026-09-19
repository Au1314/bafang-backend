package com.baffang.admin.controller;

import com.baffang.admin.dto.ComplaintAuditDTO;
import com.baffang.admin.dto.ComplaintQueryDTO;
import com.baffang.admin.service.ComplaintAuditService;
import com.baffang.admin.vo.ComplaintDetailVO;
import com.baffang.admin.vo.ComplaintListVO;
import com.baffang.admin.vo.ComplaintStatisticsVO;
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
@RequestMapping("/api/v1/admin/complaint")
@Tag(name = "投诉/故障审核管理")
public class ComplaintAuditController {

    @Autowired
    private ComplaintAuditService complaintAuditService;

    /**
     * 获取投诉/故障列表
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/list")
    @Operation(summary = "获取投诉/故障列表")
    @SystemLog(module = "投诉/故障管理", action = "获取列表", saveResult = false)
    public Result<PageResult> getComplaintList(
            ComplaintQueryDTO query,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 1);
        log.info("查询投诉/故障列表，条件={}", query);
        Page<ComplaintListVO> page = complaintAuditService.getComplaintList(query);
        return PageResult.quicklyReturn(page);
    }

    /**
     * 获取投诉/故障详情
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取投诉/故障详情")
    @SystemLog(module = "投诉/故障管理", action = "获取详情")
    public Result<ComplaintDetailVO> getComplaintDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 1);
        log.info("查询投诉/故障详情，id={}", id);
        return Result.success(complaintAuditService.getComplaintDetail(id));
    }

    /**
     * 审核投诉/故障
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核投诉/故障")
    @SystemLog(module = "投诉/故障管理", action = "审核")
    public Result<?> auditComplaint(
            @PathVariable Integer id,
            @RequestBody @Validated ComplaintAuditDTO dto,
            HttpServletRequest request) {
        
        Integer adminId = checkAdminPermission(request, 1);
        log.info("管理员[{}]审核投诉/故障[{}]，操作类型={}", adminId, id, dto.getAction());
        complaintAuditService.auditComplaint(id, dto, adminId);
        return Result.success();
    }

    /**
     * 获取统计数据
     * 权限：超级管理员(0)、普通管理员(1)
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据")
    @SystemLog(module = "投诉/故障管理", action = "获取统计", saveResult = false)
    public Result<ComplaintStatisticsVO> getStatistics(HttpServletRequest request) {
        
        checkAdminPermission(request, 1);
        log.info("查询投诉/故障统计数据");
        return Result.success(complaintAuditService.getStatistics());
    }

    // ==================== 复用：JWT权限校验方法 ====================
    
    /**
     * 检查管理员权限并返回管理员ID
     * @param request HTTP请求
     * @param maxType 允许的最大管理员类型（0-超管，1-普管，2-审核员...）
     * @return 管理员ID
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
