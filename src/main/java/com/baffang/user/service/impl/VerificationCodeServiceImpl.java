package com.baffang.user.service.impl;

import com.baffang.common.exception.BusinessException;
import com.baffang.common.sms.SmsSender;
import com.baffang.user.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现类
 * 安全整改：验证码只保存在服务端内存（有效期 + 发送间隔限流），不再返回给前端；
 * verifyCode 校验通过后立即删除，防止重放。
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    /** 验证码有效期（毫秒） */
    private static final long EXPIRE_MS = 5 * 60 * 1000L;

    /** 同一手机号两次发送的最小间隔（毫秒） */
    private static final long RESEND_INTERVAL_MS = 60 * 1000L;

    /** 验证码长度 */
    private static final int CODE_LENGTH = 4;

    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    /** 短信出口（触达整改：配置 aliyun + 密钥即真实发送；默认仅记日志） */
    private final SmsSender smsSender;

    public VerificationCodeServiceImpl(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    /**
     * 演示/开发开关：关闭时退回旧行为（验证码非空即通过），生产必须保持 true
     */
    @Value("${verification.code.enabled:true}")
    private boolean verificationEnabled;

    @Override
    public void sendVerificationCode(String phone) {
        long now = System.currentTimeMillis();
        CodeEntry old = codeStore.get(phone);
        if (old != null && now - old.createTime < RESEND_INTERVAL_MS) {
            throw new BusinessException("验证码发送过于频繁，请稍后再试");
        }

        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        codeStore.put(phone, new CodeEntry(sb.toString(), now));

        // 触达整改：通过短信出口真实发送（默认 LogSmsSender 仅记日志），绝不返回给前端
        String code = sb.toString();
        if (smsSender.enabled()) {
            boolean accepted = smsSender.send(phone, null, "{\"code\":\"" + code + "\"}");
            if (!accepted) {
                log.warn("短信发送受理失败: phone={}", maskPhone(phone));
            }
        } else {
            log.info("验证码已生成并发送至手机 {}（5 分钟内有效）", maskPhone(phone));
        }

        // 顺带清理过期项，避免长期运行下内存膨胀
        codeStore.values().removeIf(e -> now - e.createTime > EXPIRE_MS);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        if (!verificationEnabled) {
            log.warn("verification.code.enabled=false：跳过验证码校验（仅限本地演示环境）");
            return code != null && !code.isEmpty();
        }
        if (phone == null || code == null || code.isEmpty()) {
            return false;
        }
        CodeEntry entry = codeStore.get(phone);
        if (entry == null || System.currentTimeMillis() - entry.createTime > EXPIRE_MS
                || !entry.code.equals(code)) {
            return false;
        }
        // 一次性使用，防止重放
        codeStore.remove(phone);
        return true;
    }

    private String maskPhone(String phone) {
        return phone == null ? "" : phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    private static class CodeEntry {
        final String code;
        final long createTime;

        CodeEntry(String code, long createTime) {
            this.code = code;
            this.createTime = createTime;
        }
    }
}
