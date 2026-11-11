package com.baffang.user.pay;

import com.baffang.common.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 支付宝沙箱网关（手写 RSA2 协议，零 SDK 依赖）
 * - 创建支付：拼装 alipay.trade.page.pay 参数并 RSA2 签名，返回收银台跳转 URL
 * - 异步通知：SHA256withRSA 验签（排除 sign/sign_type 后按 key 升序拼接）+ app_id 校验
 * 说明：手写协议而非引入 alipay-sdk，一是减少依赖面，二是签名/验签细节对面试可完整讲述。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlipaySandboxGateway implements PayGateway {

    private static final Integer PAY_METHOD_ALIPAY = 1; // 订单 pay_method 枚举：1-支付宝

    private final PayProperties properties;

    @Override
    public boolean enabled() {
        return properties.isEnabled()
                && !properties.getAppId().isBlank()
                && !properties.getPrivateKey().isBlank()
                && !properties.getAlipayPublicKey().isBlank();
    }

    @Override
    public Integer payMethod() {
        return PAY_METHOD_ALIPAY;
    }

    @Override
    public String createPay(Order order, String clientIp) {
        Map<String, String> biz = new TreeMap<>();
        biz.put("out_trade_no", order.getOrderNo());
        biz.put("total_amount", money(order.getTotalAmount()));
        biz.put("subject", "八方互充-充电订单-" + order.getOrderNo());
        biz.put("product_code", "FAST_INSTANT_TRADE_PAY");

        Map<String, String> params = new TreeMap<>();
        params.put("app_id", properties.getAppId());
        params.put("method", "alipay.trade.page.pay");
        params.put("format", "JSON");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("version", "1.0");
        if (properties.getNotifyUrl() != null && !properties.getNotifyUrl().isBlank()) {
            params.put("notify_url", properties.getNotifyUrl());
        }
        if (properties.getReturnUrl() != null && !properties.getReturnUrl().isBlank()) {
            params.put("return_url", properties.getReturnUrl());
        }
        params.put("biz_content", toJson(biz));

        String sign = sign(params, properties.getPrivateKey());
        params.put("sign", sign);

        // 收银台 GET 跳转 URL：业务参数全部 urlencode，sign 本身也要编码
        StringBuilder url = new StringBuilder(properties.getGatewayUrl());
        url.append(properties.getGatewayUrl().contains("?") ? "&" : "?");
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) url.append('&');
            url.append(e.getKey()).append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            first = false;
        }
        log.info("支付宝沙箱支付凭据已生成：orderNo={}", order.getOrderNo());
        return url.toString();
    }

    @Override
    public NotifyResult verifyNotify(Map<String, String> params) {
        try {
            // 1. 验签：排除 sign/sign_type，按 key 升序拼接后 RSA2 验证
            String sign = params.get("sign");
            if (sign == null || sign.isBlank()) {
                log.warn("支付宝通知缺少签名");
                return null;
            }
            Map<String, String> toVerify = new TreeMap<>(params);
            toVerify.remove("sign");
            toVerify.remove("sign_type");
            String content = joinParams(toVerify);
            if (!verify(content, sign, properties.getAlipayPublicKey())) {
                log.warn("支付宝通知验签失败：orderNo={}", params.get("out_trade_no"));
                return null;
            }

            // 2. 业务校验：归属本应用 + 交易成功
            if (!properties.getAppId().equals(params.get("app_id"))) {
                log.warn("支付宝通知 app_id 不匹配：{}", params.get("app_id"));
                return null;
            }
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.info("支付宝通知交易未成功：{}，忽略", tradeStatus);
                return null;
            }

            String orderNo = params.get("out_trade_no");
            BigDecimal amount = new BigDecimal(params.getOrDefault("total_amount", "0"));
            return new NotifyResult(orderNo, amount);
        } catch (Exception e) {
            log.error("支付宝通知处理异常", e);
            return null;
        }
    }

    // ==================== RSA2 签名/验签 ====================

    /** 按参数 key 升序拼接 k=v&k=v（排除空值与 sign），与支付宝官方规则一致 */
    private String joinParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        List<String> keys = params.keySet().stream()
                .filter(k -> params.get(k) != null && !params.get(k).isEmpty())
                .sorted()
                .toList();
        for (int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i)).append('=').append(params.get(keys.get(i)));
            if (i < keys.size() - 1) sb.append('&');
        }
        return sb.toString();
    }

    private String sign(Map<String, String> params, String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(joinParams(params).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("支付宝参数签名失败，请检查应用私钥配置", e);
        }
    }

    private boolean verify(String content, String sign, String alipayPublicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(alipayPublicKeyBase64);
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(key);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            log.error("支付宝验签异常", e);
            return false;
        }
    }

    private String money(BigDecimal amount) {
        return amount == null ? "0.00" : amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    /** 极简 JSON 序列化（仅扁平字符串键值，避免引入序列化器歧义） */
    private String toJson(Map<String, String> flat) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : flat.entrySet()) {
            if (!first) sb.append(',');
            sb.append('"').append(e.getKey()).append("\":\"").append(e.getValue()).append('"');
            first = false;
        }
        return sb.append('}').toString();
    }
}
