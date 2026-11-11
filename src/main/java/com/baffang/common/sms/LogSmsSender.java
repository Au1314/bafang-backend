package com.baffang.common.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 默认短信实现：仅记录日志（本地开发/未配置短信服务商时使用）
 */
@Slf4j
@Component
@ConditionalOnMissingBean(AliyunSmsSender.class)
@ConditionalOnProperty(name = "sms.provider", havingValue = "none", matchIfMissing = true)
public class LogSmsSender implements SmsSender {

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public boolean send(String phone, String templateCode, String templateParam) {
        log.info("【短信·本地模拟】phone={}, templateCode={}, param={}", phone, templateCode, templateParam);
        return true;
    }
}
