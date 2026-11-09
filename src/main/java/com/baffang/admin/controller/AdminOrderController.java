package com.baffang.admin.controller;

import com.baffang.common.annotation.SystemLog;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.user.service.OrderService;
import com.github.pagehelper.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端订单控制器（契约补齐：订单列表供管理端查询/大屏/导出）
 */
@RestController
@RequestMapping("/api/v1/admin/order")
@Tag(name = "订单管理")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    @Operation(summary = "订单列表", description = "按用户/状态/类型筛选，分页查询")
    @SystemLog(module = "订单管理", action = "查询订单列表", saveResult = false)
    public Result<PageResult> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) Integer userId,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) Integer orderType,
                                   jakarta.servlet.http.HttpServletRequest request) {
        adminGuard.requireAdmin(1);
        Page<com.baffang.common.entity.Order> page = orderService.listOrders(userId, status, orderType, pageNum, pageSize);
        return PageResult.quicklyReturn(page);
    }

    private final com.baffang.framework.permission.AdminGuard adminGuard;
}
