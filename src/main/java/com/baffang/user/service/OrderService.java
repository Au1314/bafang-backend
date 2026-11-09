package com.baffang.user.service;

import com.baffang.common.entity.Order;
import com.github.pagehelper.Page;

import java.math.BigDecimal;

/**
 * 订单服务（契约补齐：order 交易闭环——状态机 + 余额/积分支付 + 碳积分奖励）
 */
public interface OrderService {

    /**
     * 创建订单（状态 0-待支付；电费 = 电量 × 单价）
     */
    Order createOrder(Integer userId, Integer orderType, Integer chargingStationId,
                      Integer chargingStationType, Integer v2vCallId,
                      BigDecimal electricity, BigDecimal unitPrice, String remark);

    /**
     * 开始充电（0-待支付 → 1-充电中）
     */
    void startCharging(Integer orderId, Integer userId);

    /**
     * 完成充电（1-充电中 → 2-完成待支付），按电量奖励碳积分（1kWh = 10 积分）
     */
    void finishCharging(Integer orderId, Integer userId, BigDecimal electricity,
                        BigDecimal unitPrice, Integer durationMinutes);

    /**
     * 支付订单（0/2 → 3-已完成）
     *
     * @param payMethod 2-余额，3-积分抵扣（1 积分 = 1 元）
     */
    void payOrder(Integer orderId, Integer userId, Integer payMethod);

    /**
     * 取消订单（0/2 → 4-已取消）
     */
    void cancelOrder(Integer orderId, Integer userId);

    /**
     * 我的订单列表
     */
    Page<Order> myOrders(Integer userId, Integer status, Integer orderType,
                         Integer pageNum, Integer pageSize);

    /**
     * 订单列表（管理端，按用户/状态/类型筛选）
     */
    Page<Order> listOrders(Integer userId, Integer status, Integer orderType,
                           Integer pageNum, Integer pageSize);
}
