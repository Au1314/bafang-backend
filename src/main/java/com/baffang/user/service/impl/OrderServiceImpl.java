package com.baffang.user.service.impl;

import com.baffang.common.entity.Order;
import com.baffang.common.entity.UserInfo;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.mapper.OrderMapper;
import com.baffang.user.mapper.UserMapper;
import com.baffang.user.pay.AlipaySandboxGateway;
import com.baffang.user.pay.PayGateway;
import com.baffang.user.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务实现（契约补齐）
 * 状态机：0-待支付 → 1-充电中 → 2-完成待支付 → 3-已完成（或 4-已取消）
 * 关键写操作都带乐观状态条件（影响行数 0 即拒绝），防并发重复操作；
 * 支付扣余额/积分与订单状态更新在同一事务，失败回滚。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** 充电完成碳积分奖励：1 kWh = 10 积分 */
    private static final int POINTS_PER_KWH = 10;
    /** 积分抵扣比例：1 积分 = 1 元 */
    private static final BigDecimal POINTS_TO_YUAN = BigDecimal.ONE;

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final AlipaySandboxGateway alipayGateway;

    @Override
    public Order createOrder(Integer userId, Integer orderType, Integer chargingStationId,
                             Integer chargingStationType, Integer v2vCallId,
                             BigDecimal electricity, BigDecimal unitPrice, String remark) {
        if (orderType == null || orderType < 0 || orderType > 2) {
            throw new BusinessException("订单类型不合法");
        }
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setOrderType(orderType);
        order.setChargingStationId(chargingStationId);
        order.setChargingStationType(chargingStationType);
        order.setV2vCallId(v2vCallId);
        order.setStatus(0);
        order.setRemark(remark);

        if (electricity != null && unitPrice != null) {
            if (electricity.signum() <= 0) {
                throw new BusinessException("充电电量必须大于0");
            }
            BigDecimal electricityFee = electricity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
            order.setElectricity(electricity);
            order.setUnitPrice(unitPrice);
            order.setElectricityFee(electricityFee);
            order.setTotalAmount(electricityFee);
            order.setActualAmount(electricityFee);
        } else {
            // 充电过程类订单：完成时回填金额
            order.setTotalAmount(BigDecimal.ZERO);
            order.setActualAmount(BigDecimal.ZERO);
        }

        if (orderMapper.insert(order) != 1) {
            throw new BusinessException("订单创建失败");
        }
        log.info("用户[{}]创建订单[{}]，类型={}，金额={}", userId, order.getOrderNo(), orderType, order.getTotalAmount());
        return order;
    }

    @Override
    public void startCharging(Integer orderId, Integer userId) {
        Order order = requireOrder(orderId, userId);
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待支付订单可以开始充电");
        }
        if (orderMapper.startCharging(orderId) == 0) {
            throw new BusinessException("操作失败，订单状态已变更，请刷新重试");
        }
        log.info("订单[{}]开始充电", order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishCharging(Integer orderId, Integer userId, BigDecimal electricity,
                               BigDecimal unitPrice, Integer durationMinutes) {
        requireOrder(orderId, userId);
        if (electricity == null || electricity.signum() <= 0) {
            throw new BusinessException("充电电量必须大于0");
        }
        BigDecimal price = unitPrice;
        if (price == null) {
            Order current = orderMapper.selectById(orderId);
            price = current.getUnitPrice();
        }
        if (price == null) {
            throw new BusinessException("缺少单价信息，无法结算");
        }
        BigDecimal electricityFee = electricity.multiply(price).setScale(2, RoundingMode.HALF_UP);
        if (orderMapper.finishCharging(orderId, durationMinutes, electricity, price,
                electricityFee, electricityFee) == 0) {
            throw new BusinessException("操作失败，订单状态已变更，请刷新重试");
        }

        // 碳积分奖励：按电量折算（1kWh=10积分），失败不阻塞结算
        try {
            int points = electricity.multiply(BigDecimal.valueOf(POINTS_PER_KWH)).intValue();
            if (points > 0) {
                userMapper.addPoints(userId, points);
                log.info("订单[{}]充电完成，奖励碳积分 {}", orderId, points);
            }
        } catch (Exception e) {
            log.error("碳积分奖励失败: orderId={}", orderId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Integer orderId, Integer userId, Integer payMethod) {
        Order order = requireOrder(orderId, userId);
        if (order.getStatus() != 0 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不可支付");
        }
        BigDecimal amount = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        if (amount.signum() < 0) {
            throw new BusinessException("订单金额异常");
        }

        if (Integer.valueOf(2).equals(payMethod)) {
            // 余额支付：乐观扣减（余额不足影响行数为 0）
            if (userMapper.deductBalance(userId, amount) == 0) {
                throw new BusinessException("余额不足，请先充值");
            }
        } else if (Integer.valueOf(3).equals(payMethod)) {
            // 积分抵扣：1 积分 = 1 元
            int points = amount.setScale(0, RoundingMode.CEILING).intValue();
            if (userMapper.deductPoints(userId, points) == 0) {
                throw new BusinessException("积分不足");
            }
        } else {
            throw new BusinessException("暂只支持余额支付或积分抵扣（微信/支付宝接入沙箱后开放）");
        }

        if (orderMapper.pay(orderId, payMethod, amount) == 0) {
            // 乐观条件失败（并发支付/取消）：抛异常回滚已执行的余额扣减
            throw new BusinessException("订单状态已变更，支付未完成，请刷新重试");
        }
        log.info("订单[{}]支付完成，方式={}，金额={}", order.getOrderNo(), payMethod, amount);
    }

    @Override
    public String createGatewayPayUrl(Integer orderId, Integer userId, Integer payMethod) {
        Order order = requireOrder(orderId, userId);
        if (order.getStatus() != 0 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不可支付");
        }
        if (!alipayGateway.enabled()) {
            throw new BusinessException("支付宝支付未启用：请配置沙箱参数（ALIPAY_APP_ID 等环境变量）");
        }
        if (!alipayGateway.payMethod().equals(payMethod)) {
            throw new BusinessException("不支持的支付渠道");
        }
        return alipayGateway.createPay(order, null);
    }

    @Override
    public boolean handleAlipayNotify(Map<String, String> params) {
        PayGateway.NotifyResult result = alipayGateway.verifyNotify(params);
        if (result == null) {
            return false;
        }
        Order order = orderMapper.selectByOrderNo(result.orderNo());
        if (order == null) {
            log.warn("支付宝通知的订单不存在：{}", result.orderNo());
            return false;
        }
        // 幂等：已完成的订单重复通知直接按成功处理
        if (order.getStatus() == 3) {
            log.info("订单[{}]已完成，重复通知幂等处理", order.getOrderNo());
            return true;
        }
        // 金额校验：实付不得低于应付
        BigDecimal payable = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        if (result.paidAmount().compareTo(payable) < 0) {
            log.warn("通知金额低于应付：{} < {}，orderNo={}", result.paidAmount(), payable, order.getOrderNo());
            return false;
        }
        boolean ok = orderMapper.pay(order.getId(), 1, payable) > 0;
        if (ok) {
            log.info("订单[{}]支付宝通知入账成功，金额={}", order.getOrderNo(), payable);
        }
        return ok;
    }

    @Override
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = requireOrder(orderId, userId);
        if (order.getStatus() != 0 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不可取消");
        }
        if (orderMapper.cancel(orderId) == 0) {
            throw new BusinessException("操作失败，订单状态已变更，请刷新重试");
        }
        log.info("订单[{}]已取消", order.getOrderNo());
    }

    @Override
    public Page<Order> myOrders(Integer userId, Integer status, Integer orderType,
                                Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = orderMapper.selectList(userId, status, orderType);
        Page<Order> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        for (Map<String, Object> map : mapList) {
            page.add(mapToOrder(map));
        }
        return page;
    }

    @Override
    public Page<Order> listOrders(Integer userId, Integer status, Integer orderType,
                                  Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = orderMapper.selectList(userId, status, orderType);
        Page<Order> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        for (Map<String, Object> map : mapList) {
            page.add(mapToOrder(map));
        }
        return page;
    }

    // ==================== 私有工具 ====================

    private Order requireOrder(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人订单");
        }
        return order;
    }

    /** 订单号：BO + yyyyMMddHHmmss + 4位随机数（order_no 唯一键兜底） */
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "BO" + time + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    @SuppressWarnings("unchecked")
    private Order mapToOrder(Map<String, Object> map) {
        Order order = new Order();
        order.setId(((Number) map.get("id")).intValue());
        order.setOrderNo((String) map.get("orderNo"));
        order.setUserId(toInt(map.get("userId")));
        order.setOrderType(toInt(map.get("orderType")));
        order.setChargingStationId(toInt(map.get("chargingStationId")));
        order.setChargingStationType(toInt(map.get("chargingStationType")));
        order.setElectricity(toBigDecimal(map.get("electricity")));
        order.setUnitPrice(toBigDecimal(map.get("unitPrice")));
        order.setTotalAmount(toBigDecimal(map.get("totalAmount")));
        order.setActualAmount(toBigDecimal(map.get("actualAmount")));
        order.setStatus(toInt(map.get("status")));
        order.setPayMethod(toInt(map.get("payMethod")));
        order.setDuration(toInt(map.get("duration")));
        Object createTime = map.get("createTime");
        if (createTime instanceof java.util.Date d) {
            order.setCreateTime(d);
        }
        Object startTime = map.get("startTime");
        if (startTime instanceof java.util.Date d) {
            order.setStartTime(d);
        }
        Object endTime = map.get("endTime");
        if (endTime instanceof java.util.Date d) {
            order.setEndTime(d);
        }
        return order;
    }

    private Integer toInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v instanceof BigDecimal bd) {
            return bd;
        }
        if (v instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return null;
    }
}
