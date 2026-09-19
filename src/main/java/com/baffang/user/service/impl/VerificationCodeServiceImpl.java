package com.baffang.user.service.impl;

import com.baffang.user.dto.VerificationCodeDTO;
import com.baffang.user.service.VerificationCodeService;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 验证码服务实现类
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    /**
     * 验证码有效期（秒）
     */
    private static final Integer EXPIRE_TIME = 300;

    /**
     * 验证码长度
     */
    private static final Integer CODE_LENGTH = 4;

    /**
     * 获取验证码
     * 
     * @param phone 手机号
     * @return 验证码和有效期信息
     */
    @Override
    public VerificationCodeDTO getVerificationCode(String phone) {
        // 生成随机验证码（开发阶段直接生成并返回）
        String verifyCode = generateRandomCode();
        
        // 创建返回对象
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setVerifyCode(verifyCode);
        verificationCodeDTO.setExpireTime(EXPIRE_TIME);
        
        // 实际项目中，这里应该调用短信发送服务发送验证码
        // 开发阶段直接返回
        return verificationCodeDTO;
    }

    /**
     * 生成指定长度的随机数字验证码
     * 
     * @return 随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}