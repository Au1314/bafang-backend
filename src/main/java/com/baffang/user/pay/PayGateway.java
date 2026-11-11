package com.baffang.user.pay;

import com.baffang.common.entity.Order;

import java.util.Map;

/**
 * 外部支付网关抽象（真实可运营整改）
 * 余额/积分属于平台内部账务，仍由 OrderService 事务内处理；
 * 本接口只承载需要第三方交互的渠道（支付宝/微信），职责：
 * 1. pay：为订单生成支付凭据（跳转 URL / 二维码码串）
 * 2. verify：验证异步通知的签名与业务状态（防伪造回调）
 * 实现方必须保证：验签失败返回 false、幂等（同一订单重复通知不产生副作用）
 */
public interface PayGateway {

    /**
     * 是否可用（配置齐全才注册/启用，缺商户配置时自动降级为不可用）
     */
    boolean enabled();

    /**
     * 渠道标识，对应订单 pay_method 枚举：0-微信，1-支付宝
     */
    Integer payMethod();

    /**
     * 创建支付凭据
     *
     * @param order 待支付订单（状态 0/2）
     * @return 渠道相关凭据：支付宝为收银台跳转 URL，微信为二维码码串
     */
    String createPay(Order order, String clientIp);

    /**
     * 验证异步通知并提取业务字段
     *
     * @param params 通知原始参数（form 表单键值对）
     * @return 验签通过且交易成功时，返回订单号与实付金额；否则返回 null
     */
    NotifyResult verifyNotify(Map<String, String> params);

    /**
     * 通知解析结果
     */
    record NotifyResult(String orderNo, java.math.BigDecimal paidAmount) {
    }
}
