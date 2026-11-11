package com.baffang.user.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱配置（敏感值全部走环境变量占位符）
 * 沙箱控制台：https://open.alipay.com → 沙箱（应用网关地址/RSA2 密钥）
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay.alipay")
public class PayProperties {

    /** 总开关：false 时支付宝网关不可用，支付仅走余额/积分 */
    private boolean enabled = false;

    /** 沙箱/正式 APPID */
    private String appId = "";

    /** 应用私钥（PKCS8，一行 base64） */
    private String privateKey = "";

    /** 支付宝公钥（用于验签，非证书模式） */
    private String alipayPublicKey = "";

    /** 网关地址：沙箱 https://openapi-sandbox.dl.alipaydev.com/gateway.do */
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /** 异步通知地址（必须是公网可达 URL） */
    private String notifyUrl = "";

    /** 支付完成后的前端回跳地址 */
    private String returnUrl = "";
}
