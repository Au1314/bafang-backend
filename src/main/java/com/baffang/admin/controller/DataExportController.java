package com.baffang.admin.controller;

import com.baffang.admin.service.DataExportService;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.ratelimit.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据导出控制器（真实 CSV 文件流）
 * 此前后端无任何导出接口、前端整页为模拟实现；本控制器按前端既有封装的路径实现真导出，
 * 并通过 @SystemLog 自动记录导出审计（module=数据导出）。
 */
@RestController
@RequestMapping("/api/v1/admin/data/export")
@Tag(name = "数据导出")
@RequiredArgsConstructor
public class DataExportController {

    private final DataExportService dataExportService;

    /**
     * 导出审核记录 CSV
     */
    @PostMapping("/audit-records")
    @Operation(summary = "导出审核记录")
    @SystemLog(module = "数据导出", action = "导出审核记录", saveResult = false)
    @RateLimit(limit = 10, windowSeconds = 60)
    public ResponseEntity<byte[]> exportAuditRecords(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request) {
        return dataExportService.exportAuditRecords(params);
    }

    /**
     * 导出用户数据 CSV（前端"订单&用户数据"卡片）
     */
    @PostMapping("/order-user")
    @Operation(summary = "导出用户数据")
    @SystemLog(module = "数据导出", action = "导出用户数据", saveResult = false)
    @RateLimit(limit = 10, windowSeconds = 60)
    public ResponseEntity<byte[]> exportUsers(HttpServletRequest request) {
        return dataExportService.exportUsers();
    }

    /**
     * 导出订单明细 CSV
     */
    @PostMapping("/order-details")
    @Operation(summary = "导出订单明细")
    @SystemLog(module = "数据导出", action = "导出订单明细", saveResult = false)
    @RateLimit(limit = 10, windowSeconds = 60)
    public ResponseEntity<byte[]> exportOrders(
            @RequestBody(required = false) Map<String, Object> params,
            HttpServletRequest request) {
        return dataExportService.exportOrders(params);
    }
}
