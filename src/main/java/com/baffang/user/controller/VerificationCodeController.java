package com.baffang.user.controller;

import com.baffang.common.result.Result;
import com.baffang.framework.permission.annotation.RequirePermission;
import com.baffang.user.dto.GetVerificationCodeDTO;
import com.baffang.user.dto.VerificationCodeDTO;
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
     * 
     * @param getVerificationCodeDTO 请求参数，包含手机号
     * @return 验证码和有效期信息
     */
    @PostMapping("/client/getVerificationcode")
    @RequirePermission(needLogin = false, needPermission = false)
    public Result<VerificationCodeDTO> getVerificationCode(@RequestBody GetVerificationCodeDTO getVerificationCodeDTO) {
        // 调用Service层方法获取验证码
        VerificationCodeDTO verificationCodeDTO = verificationCodeService
                .getVerificationCode(getVerificationCodeDTO.getPhone());
        // 返回结果
        String maskedPhone = getVerificationCodeDTO.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        Result<VerificationCodeDTO> result = Result.success(verificationCodeDTO);
        result.setMsg("验证码已发送至手机" + maskedPhone);
        return result;
    }
}