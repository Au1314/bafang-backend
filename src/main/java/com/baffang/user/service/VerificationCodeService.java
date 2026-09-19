package com.baffang.user.service;

import com.baffang.user.dto.VerificationCodeDTO;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    /**
     * 获取验证码
     * 
     * @param phone 手机号
     * @return 验证码和有效期信息
     */
    VerificationCodeDTO getVerificationCode(String phone);
}