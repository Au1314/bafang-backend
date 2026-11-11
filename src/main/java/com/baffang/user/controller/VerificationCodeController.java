package com.baffang.user.controller;

import com.baffang.common.result.Result;
import com.baffang.framework.permission.annotation.RequirePermission;
import com.baffang.user.dto.GetVerificationCodeDTO;
import com.baffang.user.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取验证码控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 获取验证码接口
     * 安全整改：验证码只保存在服务端，不再随响应返回
     *
     * @param getVerificationCodeDTO 请求参数，包含手机号
     * @return 发送结果提示
     */
    @PostMapping("/client/getVerificationcode")
    @RequirePermission(needLogin = false, needPermission = false)
    @com.baffang.common.ratelimit.RateLimit(limit = 5, windowSeconds = 60) // 防短信轰炸：60秒5次/IP（服务层另有60秒同号限发）
    public Result<Void> getVerificationCode(@RequestBody GetVerificationCodeDTO getVerificationCodeDTO) {
        // 调用Service层方法生成并保存验证码
        verificationCodeService.sendVerificationCode(getVerificationCodeDTO.getPhone());
        // 响应只含脱敏手机号提示，不携带验证码本身
        String maskedPhone = getVerificationCodeDTO.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        Result<Void> result = Result.success();
        result.setMsg("验证码已发送至手机" + maskedPhone + "，5 分钟内有效");
        return result;
    }
}
