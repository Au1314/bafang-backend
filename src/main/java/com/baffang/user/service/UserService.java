package com.baffang.user.service;

import com.baffang.user.vo.RegisterVO;
import com.baffang.user.dto.RegisterDTO;
import com.baffang.user.dto.UpdatePrivacySettingsDTO;
import com.baffang.user.vo.LoginVO;
import com.baffang.user.vo.PrivacySettingsVO;
import com.baffang.user.dto.LoginDTO;
import com.baffang.user.dto.LoginByCodeDTO;
import com.baffang.user.vo.ProfileVO;
import com.baffang.user.dto.UpdateProfileDTO;
import com.baffang.user.dto.ChangePasswordDTO;
import com.baffang.user.dto.ResetPasswordDTO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * 
     * @param registerDTO 注册请求参数
     * @return 注册成功后的用户信息
     */
    RegisterVO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * 
     * @param loginDTO 登录请求参数
     * @return 登录成功后的用户信息和token
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户验证码登录
     * 
     * @param loginByCodeDTO 验证码登录请求参数
     * @return 登录成功后的用户信息和token
     */
    LoginVO loginByCode(LoginByCodeDTO loginByCodeDTO);

    /**
     * 获取用户个人信息
     * 
     * @param userId 用户ID
     * @return 用户个人信息
     */
    ProfileVO getProfile(Integer userId);

    /**
     * 更新用户个人信息
     * 
     * @param userId           用户ID
     * @param updateProfileDTO 更新个人信息请求参数
     */
    void updateProfile(Integer userId, UpdateProfileDTO updateProfileDTO);

    /**
     * 修改密码
     * 
     * @param userId            用户ID
     * @param changePasswordDTO 修改密码请求参数
     */
    void changePassword(Integer userId, ChangePasswordDTO changePasswordDTO);

    /**
     * 获取用户隐私设置
     * 
     * @param userId 用户ID
     * @return 用户隐私设置
     */
    PrivacySettingsVO getPrivacySettings(Integer userId);

    /**
     * 更新用户隐私设置
     * 
     * @param userId                   用户ID
     * @param updatePrivacySettingsDTO 更新隐私设置请求参数
     * @return 更新后的隐私设置
     */
    PrivacySettingsVO updatePrivacySettings(Integer userId, UpdatePrivacySettingsDTO updatePrivacySettingsDTO);

    /**
     * 重置密码
     * 
     * @param resetPasswordDTO 重置密码请求参数
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
