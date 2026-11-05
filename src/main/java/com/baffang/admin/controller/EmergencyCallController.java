package com.baffang.admin.controller;

import com.baffang.admin.dto.*;
import com.baffang.admin.service.EmergencyCallService;
import com.baffang.admin.vo.*;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/emergency")
@Tag(name = "紧急响应管理")
@RequiredArgsConstructor
public class EmergencyCallController {

    private final EmergencyCallService emergencyCallService;

    @GetMapping("/call/pending")
    @Operation(summary = "获取待响应呼叫列表（按距离排序）")
    @SystemLog(module = "紧急响应", action = "获取待响应列表", saveResult = false)
    public Result<PageResult> getPendingList(@Validated EmergencyCallPendingQueryDTO queryDTO, 
                                              HttpServletRequest request) {
        checkAdminPermission(request, 4); // 紧急响应人员及以上
        Page<EmergencyCallListVO> page = emergencyCallService.getPendingList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/call/list")
    @Operation(summary = "获取呼叫列表")
    @SystemLog(module = "紧急响应", action = "获取呼叫列表", saveResult = false)
    public Result<PageResult> getCallList(EmergencyCallQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 4);
        Page<EmergencyCallListVO> page = emergencyCallService.getCallList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/call/{id}")
    @Operation(summary = "获取呼叫详情")
    @SystemLog(module = "紧急响应", action = "获取呼叫详情")
    public Result<EmergencyCallDetailVO> getCallDetail(
            @Parameter(description = "呼叫ID") @PathVariable Integer id,
            HttpServletRequest request) {
        checkAdminPermission(request, 4);
        return Result.success(emergencyCallService.getCallDetail(id));
    }

    @PostMapping("/call/{id}/respond")
    @Operation(summary = "响应紧急呼叫")
    @SystemLog(module = "紧急响应", action = "响应呼叫")
    public Result<?> respondCall(
            @Parameter(description = "呼叫ID") @PathVariable Integer id,
            @RequestBody @Validated EmergencyRespondDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 4);
        emergencyCallService.respondCall(id, dto, adminId);
        return Result.success();
    }

    @PutMapping("/call/{id}/target")
    @Operation(summary = "更新目标位置")
    @SystemLog(module = "紧急响应", action = "更新目标位置")
    public Result<?> updateTarget(
            @Parameter(description = "呼叫ID") @PathVariable Integer id,
            @RequestBody @Validated EmergencyTargetDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 4);
        emergencyCallService.updateTarget(id, dto, adminId);
        return Result.success();
    }

    @PostMapping("/call/{id}/complete")
    @Operation(summary = "完成呼叫")
    @SystemLog(module = "紧急响应", action = "完成呼叫")
    public Result<?> completeCall(
            @Parameter(description = "呼叫ID") @PathVariable Integer id,
            @RequestBody(required = false) EmergencyCompleteDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 4);
        if (dto == null) {
            dto = new EmergencyCompleteDTO();
        }
        emergencyCallService.completeCall(id, dto, adminId);
        return Result.success();
    }

    @PostMapping("/call/{id}/cancel")
    @Operation(summary = "取消呼叫")
    @SystemLog(module = "紧急响应", action = "取消呼叫")
    public Result<?> cancelCall(
            @Parameter(description = "呼叫ID") @PathVariable Integer id,
            @RequestBody @Validated EmergencyCancelDTO dto,
            HttpServletRequest request) {
                Integer adminId = checkAdminPermission(request, 4);
        emergencyCallService.cancelCall(id, dto, adminId);
        return Result.success();
    }

    @GetMapping("/call/my-responses")
    @Operation(summary = "获取我的响应记录")
    @SystemLog(module = "紧急响应", action = "获取我的响应记录", saveResult = false)
    public Result<PageResult> getMyResponses(
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 4);
        Page<EmergencyCallListVO> page = emergencyCallService.getMyResponses(adminId, status, startTime, endTime, pageNum, pageSize);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取紧急响应统计")
    public Result<EmergencyStatisticsVO> getStatistics(HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 4);
        return Result.success(emergencyCallService.getStatistics(adminId));
    }

    // ==================== JWT权限校验方法 ====================

    /**
     * 校验管理员权限
     * @param maxType 最大允许的管理员类型（0-超管，1-普管，2-审核员，3-论坛管理员，4-紧急响应人员）
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
