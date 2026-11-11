package com.baffang.common.sms;

/**
 * 短信发送抽象（触达能力整改）
 * 验证码/紧急呼叫等场景的短信出口；默认实现为 LogSmsSender（仅记日志），
 * 配置 sms.provider=aliyun 且密钥齐全时切换为真实发送。
 */
public interface SmsSender {

    /**
     * 是否真实发送（false 时调用方保持原有行为——仅服务端日志）
     */
    boolean enabled();

    /**
     * 发送模板短信
     *
     * @param phone        手机号
     * @param templateCode 渠道方模板编码
     * @param templateParam 模板参数 JSON（如 {"code":"1234"}）
     * @return 是否受理成功
     */
    boolean send(String phone, String templateCode, String templateParam);
}
