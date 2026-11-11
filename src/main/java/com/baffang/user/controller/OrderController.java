package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.Order;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.service.OrderService;
import com.github.pagehelper.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户端订单控制器（契约补齐：order 交易闭环）
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user/order")
@Tag(name = "充电订单")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单请求
     */
    @Data
    public static class OrderCreateDTO {
        /** 订单类型：0-公桩充电，1-私桩充电，2-V2V充电 */
        private Integer orderType;
        private Integer chargingStationId;
        private Integer chargingStationType;
        private Integer v2vCallId;
        private BigDecimal electricity;
        private BigDecimal unitPrice;
        private String remark;
    }

    /**
     * 充电结算请求
     */
    @Data
    public static class OrderFinishDTO {
        private BigDecimal electricity;
        private BigDecimal unitPrice;
        private Integer durationMinutes;
    }

    /**
     * 支付请求
     */
    @Data
    public static class OrderPayDTO {
        /** 2-余额，3-积分抵扣 */
        private Integer payMethod;
    }

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<Order> create(@RequestBody OrderCreateDTO dto,
                                @RequestHeader("Authorization") String authorization) {
        Integer userId = currentUserId(authorization);
        return Result.success(orderService.createOrder(userId, dto.getOrderType(),
                dto.getChargingStationId(), dto.getChargingStationType(), dto.getV2vCallId(),
                dto.getElectricity(), dto.getUnitPrice(), dto.getRemark()));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开始充电")
    public Result<Void> start(@PathVariable Integer id, @RequestHeader("Authorization") String authorization) {
        orderService.startCharging(id, currentUserId(authorization));
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "完成充电结算")
    public Result<Void> finish(@PathVariable Integer id,
                               @RequestBody OrderFinishDTO dto,
                               @RequestHeader("Authorization") String authorization) {
        orderService.finishCharging(id, currentUserId(authorization),
                dto.getElectricity(), dto.getUnitPrice(), dto.getDurationMinutes());
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "余额/积分同步入账；支付宝返回收银台跳转 URL，等待异步通知入账")
    public Result<Object> pay(@PathVariable Integer id,
                              @RequestBody OrderPayDTO dto,
                              @RequestHeader("Authorization") String authorization) {
        Integer userId = currentUserId(authorization);
        if (dto.getPayMethod() == null) {
            throw new BusinessException("请选择支付方式");
        }
        // 外部渠道（1-支付宝）：返回支付凭据，订单等待异步通知
        if (Integer.valueOf(1).equals(dto.getPayMethod())) {
            String payUrl = orderService.createGatewayPayUrl(id, userId, dto.getPayMethod());
            return Result.success(Map.of("payMethod", 1, "payUrl", payUrl));
        }
        // 内部账务（2-余额，3-积分）：事务内同步入账
        orderService.payOrder(id, userId, dto.getPayMethod());
        return Result.success();
    }

    /**
     * 支付宝异步通知（公开端点：以 RSA2 验签作为身份与完整性保障，签名即鉴权）
     * 渠道方要求响应纯文本 success 才停止重发
     */
    @PostMapping("/pay/notify/alipay")
    @Operation(summary = "支付宝异步通知", hidden = true)
    public String alipayNotify(@RequestParam java.util.Map<String, String> params) {
        boolean ok = orderService.handleAlipayNotify(params);
        return ok ? "success" : "fail";
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancel(@PathVariable Integer id, @RequestHeader("Authorization") String authorization) {
        orderService.cancelOrder(id, currentUserId(authorization));
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的订单列表")
    public Result<PageResult> my(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) Integer status,
                                 @RequestParam(required = false) Integer orderType,
                                 @RequestHeader("Authorization") String authorization) {
        Integer userId = currentUserId(authorization);
        Page<com.baffang.common.entity.Order> page = orderService.myOrders(userId, status, orderType, pageNum, pageSize);
        return PageResult.quicklyReturn(page);
    }

    private Integer currentUserId(String authorization) {
        String token = authorization == null ? "" : authorization.replace(JwtConstants.TOKEN_PREFIX, "").trim();
        try {
            Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
            if (userId == null) {
                throw new BusinessException(com.baffang.common.enums.ErrorMsg.TOKEN_INVALID);
            }
            return userId;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(com.baffang.common.enums.ErrorMsg.TOKEN_INVALID);
        }
    }
}
