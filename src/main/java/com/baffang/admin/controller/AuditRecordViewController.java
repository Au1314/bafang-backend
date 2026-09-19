package com.baffang.admin.controller;

import com.baffang.admin.dto.AuditRecordQueryDTO;
import com.baffang.admin.service.AuditRecordViewService;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.AuditRecordDetailVO;
import com.baffang.admin.vo.AuditRecordListVO;
import com.baffang.admin.vo.AuditRecordStatisticsVO;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/audit/record")
@Tag(name = "审核记录查看")
@RequiredArgsConstructor
public class AuditRecordViewController {

    private final AuditRecordViewService auditRecordViewService;

    /**
     * 获取审核记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取审核记录列表", description = "分页查询所有类型的审核记录")
    @SystemLog(module = "审核记录管理", action = "获取列表", saveResult = false)
    public Result<PageResult> getList(
            @Parameter(description = "查询参数") AuditRecordQueryDTO queryDTO,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        Page<AuditRecordListVO> page = auditRecordViewService.getList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    /**
     * 获取审核记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取审核记录详情", description = "查看审核记录详细信息，包含申请人信息和审核日志")
    @SystemLog(module = "审核记录管理", action = "获取详情")
    public Result<AuditRecordDetailVO> getDetail(
            @Parameter(description = "审核记录ID") @PathVariable Integer id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        return Result.success(auditRecordViewService.getDetail(id));
    }

    /**
     * 获取审核操作日志
     */
    @GetMapping("/{id}/logs")
    @Operation(summary = "获取审核操作日志", description = "查看指定审核记录的所有操作日志")
    public Result<List<AuditLogVO>> getAuditLogs(
            @Parameter(description = "审核记录ID") @PathVariable Integer id,
            HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        return Result.success(auditRecordViewService.getAuditLogs(id));
    }

    /**
     * 获取审核统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取审核统计数据", description = "获取各类型审核的统计数据")
    public Result<AuditRecordStatisticsVO> getStatistics(HttpServletRequest request) {
        
        checkAdminPermission(request, 2);
        
        return Result.success(auditRecordViewService.getStatistics());
    }

    // ==================== JWT权限校验方法 ====================

    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
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
            
            Object adminIdObj = claims.get("adminId");
            if (adminIdObj instanceof Number) {
                return ((Number) adminIdObj).intValue();
            }
            
            Object userIdObj = claims.get(JwtConstants.USER_ID_CLAIM);
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).intValue();
            }
            
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token解析失败", e);
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        }
    }
}
