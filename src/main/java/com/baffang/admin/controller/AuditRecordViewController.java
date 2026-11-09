package com.baffang.admin.controller;

import com.baffang.admin.dto.AuditCompleteDTO;
import com.baffang.admin.dto.AuditRecordQueryDTO;
import com.baffang.admin.mapper.AuditLogMapper;
import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.service.AuditRecordViewService;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.AuditRecordDetailVO;
import com.baffang.admin.vo.AuditRecordListVO;
import com.baffang.admin.vo.AuditRecordStatisticsVO;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/audit/record")
@Tag(name = "审核记录查看")
@RequiredArgsConstructor
public class AuditRecordViewController {

    private final AuditRecordViewService auditRecordViewService;
    private final AuditRecordMapper auditRecordMapper;
    private final AuditLogMapper auditLogMapper;

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

    // ==================== 审核动作（契约补齐：通用通过/拒绝/补材料，覆盖 type 0-5 全类型） ====================

    /**
     * 认领审核（待审核 → 审核中，记录当前处理人）
     */
    @PostMapping("/{id}/claim")
    @Operation(summary = "认领审核", description = "待审核记录认领为审核中，记录当前处理人")
    @SystemLog(module = "审核记录管理", action = "认领审核")
    public Result<Void> claim(
            @PathVariable Integer id,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 2);
        AuditRecord record = requireClaimableRecord(id);

        int updated = auditRecordMapper.updateStatus(id, 1, adminId);
        if (updated == 0) {
            throw new BusinessException("操作失败，请刷新重试");
        }
        insertAuditLog(id, adminId, 4, record.getCurrentStatus(), 1, "认领审核");
        log.info("审核员[{}]认领审核记录[{}]", adminId, id);
        return Result.success();
    }

    /**
     * 完成审核：approve-通过 / reject-拒绝 / supplement-要求补充材料
     */
    @PostMapping("/{id}/complete")
    @Operation(summary = "完成审核", description = "approve-通过，reject-拒绝（必填拒绝原因），supplement-要求补充材料")
    @SystemLog(module = "审核记录管理", action = "完成审核")
    public Result<Void> complete(
            @PathVariable Integer id,
            @RequestBody @Validated AuditCompleteDTO dto,
            HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 2);
        AuditRecord record = requireClaimableRecord(id);
        if (record.getCurrentStatus() == 4 && !"approve".equals(dto.getAction()) && !"reject".equals(dto.getAction())) {
            throw new BusinessException("该记录已要求补充材料，等待申请人重新提交");
        }

        int toStatus;
        switch (dto.getAction()) {
            case "approve":
                toStatus = 2;
                auditRecordMapper.finishAudit(id, toStatus, dto.getOpinion(), null);
                break;
            case "reject":
                if (!StringUtils.hasText(dto.getRejectReason())) {
                    throw new BusinessException("拒绝时必须填写拒绝原因");
                }
                toStatus = 3;
                auditRecordMapper.finishAudit(id, toStatus, dto.getOpinion(), dto.getRejectReason());
                break;
            case "supplement":
                toStatus = 4;
                auditRecordMapper.updateStatus(id, toStatus, adminId);
                break;
            default:
                throw new BusinessException("不支持的审核动作：" + dto.getAction());
        }

        insertAuditLog(id, adminId, toActionCode(dto.getAction()), record.getCurrentStatus(), toStatus, dto.getOpinion());
        log.info("审核员[{}]完成审核记录[{}]：{} → {}", adminId, id, record.getCurrentStatus(), toStatus);
        return Result.success();
    }

    /** 动作记录必须存在且处于可操作状态（0-待审核，1-审核中，4-需补充材料） */
    private AuditRecord requireClaimableRecord(Integer id) {
        AuditRecord record = auditRecordMapper.selectById(id);
        if (record == null || Integer.valueOf(1).equals(record.getIsDeleted())) {
            throw new BusinessException("审核记录不存在");
        }
        Integer status = record.getCurrentStatus();
        if (status == null || status == 2 || status == 3 || status == 5) {
            throw new BusinessException("该记录已完成审核，不可重复操作");
        }
        return record;
    }

    private int toActionCode(String action) {
        return switch (action) {
            case "approve" -> 1;   // 审核通过
            case "reject" -> 2;    // 审核拒绝
            case "supplement" -> 3; // 要求补充材料
            default -> 0;
        };
    }

    private void insertAuditLog(Integer auditId, Integer adminId, int action,
                                Integer fromStatus, Integer toStatus, String opinion) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(auditId);
        auditLog.setAdminId(adminId);
        auditLog.setAction(action);
        auditLog.setFromStatus(fromStatus);
        auditLog.setToStatus(toStatus);
        auditLog.setOpinion(opinion);
        auditLog.setCreateTime(new Date());
        auditLogMapper.insert(auditLog);
    }

    // ==================== JWT权限校验方法 ====================

    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改：统一委托 AdminGuard——按用户名查库取角色，不再信任 token claims
        return adminGuard.requireAdmin(maxType);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.baffang.framework.permission.AdminGuard adminGuard;
}
