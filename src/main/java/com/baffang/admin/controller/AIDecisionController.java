package com.baffang.admin.controller;

import com.baffang.admin.dto.AIDecisionQueryDTO;
import com.baffang.admin.dto.AIDecisionRequestDTO;
import com.baffang.admin.dto.DecisionHandleDTO;
import com.baffang.admin.service.AIDecisionService;
import com.baffang.admin.vo.AIDecisionDetailVO;
import com.baffang.admin.vo.AIDecisionListVO;
import com.baffang.admin.vo.AIDecisionResponseVO;
import com.baffang.admin.vo.AIDecisionStatisticsVO;
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
@RequestMapping("/api/v1/admin/ai/decision")
@Tag(name = "AI决策自动化")
@RequiredArgsConstructor
public class AIDecisionController {

    private final AIDecisionService aiDecisionService;

    // ==================== 查询接口 ====================

    @GetMapping("/list")
    @Operation(summary = "获取决策列表")
    @SystemLog(module = "AI决策", action = "获取决策列表", saveResult = false)
    public Result<PageResult> getDecisionList(AIDecisionQueryDTO queryDTO, HttpServletRequest request) {
        checkAdminPermission(request, 1);
        Page<AIDecisionListVO> page = aiDecisionService.getDecisionList(queryDTO);
        return PageResult.quicklyReturn(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取决策详情")
    @SystemLog(module = "AI决策", action = "获取决策详情")
    public Result<AIDecisionDetailVO> getDecisionDetail(
            @Parameter(description = "决策ID") @PathVariable Integer id,
            HttpServletRequest request) {
        checkAdminPermission(request, 1);
        return Result.success(aiDecisionService.getDecisionDetail(id));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取决策统计")
    public Result<AIDecisionStatisticsVO> getStatistics(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        return Result.success(aiDecisionService.getStatistics());
    }

    // ==================== 决策请求 ====================

    @PostMapping("/request")
    @Operation(summary = "请求AI决策")
    @SystemLog(module = "AI决策", action = "请求AI决策")
    public Result<AIDecisionResponseVO> requestDecision(
            @RequestBody @Validated AIDecisionRequestDTO request,
            HttpServletRequest httpRequest) {
        checkAdminPermission(httpRequest, 1);
        log.info("请求AI决策: type={}", request.getDecisionType());
        AIDecisionResponseVO response = aiDecisionService.requestDecision(request);
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @PostMapping("/audit/suggest/{auditId}")
    @Operation(summary = "AI审核建议")
    @SystemLog(module = "AI决策", action = "AI审核建议")
    public Result<AIDecisionResponseVO> suggestAudit(
            @Parameter(description = "审核记录ID") @PathVariable Integer auditId,
            HttpServletRequest request) {
        checkAdminPermission(request, 2);
        log.info("请求AI审核建议: auditId={}", auditId);
        AIDecisionResponseVO response = aiDecisionService.suggestAudit(auditId);
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @PostMapping("/anomaly/detect")
    @Operation(summary = "AI异常检测")
    @SystemLog(module = "AI决策", action = "AI异常检测")
    public Result<AIDecisionResponseVO> detectAnomalies(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        log.info("请求AI异常检测");
        AIDecisionResponseVO response = aiDecisionService.detectAnomalies();
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @PostMapping("/optimization/suggest")
    @Operation(summary = "AI运营优化建议")
    @SystemLog(module = "AI决策", action = "AI运营优化建议")
    public Result<AIDecisionResponseVO> suggestOptimization(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        log.info("请求AI运营优化建议");
        AIDecisionResponseVO response = aiDecisionService.suggestOptimization();
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @PostMapping("/risk/assess")
    @Operation(summary = "AI风险评估")
    @SystemLog(module = "AI决策", action = "AI风险评估")
    public Result<AIDecisionResponseVO> assessRisk(
            @Parameter(description = "业务类型") @RequestParam String businessType,
            @Parameter(description = "业务ID") @RequestParam Integer businessId,
            HttpServletRequest request) {
        checkAdminPermission(request, 1);
        log.info("请求AI风险评估: businessType={}, businessId={}", businessType, businessId);
        AIDecisionResponseVO response = aiDecisionService.assessRisk(businessType, businessId);
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    // ==================== 决策处理 ====================

    @PostMapping("/handle")
    @Operation(summary = "处理AI决策（采纳/忽略/部分采纳）")
    @SystemLog(module = "AI决策", action = "处理AI决策")
    public Result<AIDecisionResponseVO> handleDecision(
            @RequestBody @Validated DecisionHandleDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 1);
        log.info("处理AI决策: decisionId={}, action={}, adminId={}", dto.getDecisionId(), dto.getAction(), adminId);
        AIDecisionResponseVO response = aiDecisionService.handleDecision(dto, adminId);
        return response.getSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @PostMapping("/execute/{decisionId}")
    @Operation(summary = "手动执行决策动作")
    @SystemLog(module = "AI决策", action = "手动执行决策")
    public Result<String> executeActions(
            @PathVariable Integer decisionId,
            HttpServletRequest request) {
        checkAdminPermission(request, 1);
        log.info("手动执行决策动作: decisionId={}", decisionId);
        aiDecisionService.executeActions(decisionId);
        return Result.success("执行已触发");
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
