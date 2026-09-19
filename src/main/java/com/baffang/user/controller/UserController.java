package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.framework.permission.annotation.RequirePermission;
import com.baffang.user.vo.RegisterVO;
import com.baffang.user.dto.RegisterDTO;
import com.baffang.user.dto.UpdateProfileDTO;
import com.baffang.user.vo.LoginVO;
import com.baffang.user.dto.LoginDTO;
import com.baffang.user.dto.LoginByCodeDTO;
import com.baffang.user.dto.ChangePasswordDTO;
import com.baffang.user.dto.ResetPasswordDTO;
import com.baffang.user.dto.UpdatePrivacySettingsDTO;
import com.baffang.user.vo.ProfileVO;
import com.baffang.user.vo.PrivacySettingsVO;
import com.baffang.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/client/register")
    @RequirePermission(needLogin = false, needPermission = false)
    public Result<RegisterVO> register(@RequestBody RegisterDTO registerDTO) {
        // 调用Service层方法进行注册
        RegisterVO registerVO = userService.register(registerDTO);
        // 返回注册结果
        Result<RegisterVO> result = Result.success(registerVO);
        result.setMsg("注册成功，请登录");
        return result;
    }

    /**
     * 用户登录接口
     *
     * @param loginDTO 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/client/login/password")
    @RequirePermission(needLogin = false, needPermission = false)
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 调用Service层方法进行登录
        LoginVO loginVO = userService.login(loginDTO);
        // 返回登录结果
        Result<LoginVO> result = Result.success(loginVO);
        result.setMsg("登录成功");
        return result;
    }

    /**
     * 用户验证码登录接口
     *
     * @param loginByCodeDTO 验证码登录请求参数
     * @return 登录结果
     */
    @PostMapping("/client/login/code")
    @RequirePermission(needLogin = false, needPermission = false)
    public Result<LoginVO> loginByCode(@RequestBody LoginByCodeDTO loginByCodeDTO) {
        // 调用Service层方法进行验证码登录
        LoginVO loginVO = userService.loginByCode(loginByCodeDTO);
        // 返回登录结果
        Result<LoginVO> result = Result.success(loginVO);
        result.setMsg("登录成功");
        return result;
    }

    /**
     * 获取个人信息接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @return 用户个人信息
     */
    @GetMapping("/client/profile")
    public Result<ProfileVO> getProfile(@RequestHeader("Authorization") String authorization) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取用户个人信息
        ProfileVO profileVO = userService.getProfile(userId);

        // 4. 返回结果
        Result<ProfileVO> result = Result.success(profileVO);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 更新个人信息接口
     *
     * @param authorization    请求头中的Authorization令牌
     * @param updateProfileDTO 更新个人信息请求参数
     * @return 更新结果
     */
    @PutMapping("/client/profile")
    public Result<?> updateProfile(@RequestHeader("Authorization") String authorization,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法更新用户个人信息
        userService.updateProfile(userId, updateProfileDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("信息更新成功");
        return result;
    }

    /**
     * 修改密码接口
     *
     * @param authorization     请求头中的Authorization令牌
     * @param changePasswordDTO 修改密码请求参数
     * @return 修改结果
     */
    @PutMapping("/client/ChangePassword")
    public Result<?> changePassword(@RequestHeader("Authorization") String authorization,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法修改密码
        userService.changePassword(userId, changePasswordDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("密码更新成功");
        return result;
    }

    /**
     * 获取隐私设置接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @return 用户隐私设置
     */
    @GetMapping("/client/PrivacySettings")
    public Result<PrivacySettingsVO> getPrivacySettings(@RequestHeader("Authorization") String authorization) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取用户隐私设置
        PrivacySettingsVO privacySettingsVO = userService.getPrivacySettings(userId);

        // 4. 返回结果
        Result<PrivacySettingsVO> result = Result.success(privacySettingsVO);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 更新隐私设置接口
     *
     * @param authorization            请求头中的Authorization令牌
     * @param updatePrivacySettingsDTO 更新隐私设置请求参数
     * @return 更新后的用户隐私设置
     */
    @PutMapping("/client/PrivacySettings")
    public Result<PrivacySettingsVO> updatePrivacySettings(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdatePrivacySettingsDTO updatePrivacySettingsDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法更新用户隐私设置
        PrivacySettingsVO privacySettingsVO = userService.updatePrivacySettings(userId, updatePrivacySettingsDTO);

        // 4. 返回结果
        Result<PrivacySettingsVO> result = Result.success(privacySettingsVO);
        result.setMsg("隐私设置更新成功");
        return result;
    }

    /**
     * 重置密码接口
     *
     * @param resetPasswordDTO 重置密码请求参数
     * @return 重置结果
     */
    @PutMapping("/client/ResetPassword")
    @RequirePermission(needLogin = false, needPermission = false)
    public Result<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        // 1. 调用Service层方法重置密码
        userService.resetPassword(resetPasswordDTO);

        // 2. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("密码重置成功");
        return result;
    }
}
