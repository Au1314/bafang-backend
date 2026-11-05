package com.baffang.user.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {

    /**
     * 发送验证码（安全整改：验证码只保存在服务端，带 60 秒发送间隔限流与 5 分钟有效期，
     * 不再随响应返回给调用方）
     *
     * @param phone 手机号
     */
    void sendVerificationCode(String phone);

    /**
     * 校验验证码（校验通过后立即失效，防止重放）
     *
     * @param phone 手机号
     * @param code  用户输入的验证码
     * @return 是否通过
     */
    boolean verifyCode(String phone, String code);
}
