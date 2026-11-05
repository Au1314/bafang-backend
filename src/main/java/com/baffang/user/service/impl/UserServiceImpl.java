package com.baffang.user.service.impl;

import com.baffang.common.entity.UserInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.UserRoleEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.util.PasswordUtil;
import com.baffang.common.util.JwtUtil;
import com.baffang.common.constant.JwtConstants;
import com.baffang.user.vo.RegisterVO;
import com.baffang.user.dto.RegisterDTO;
import com.baffang.user.vo.LoginVO;
import com.baffang.user.dto.LoginDTO;
import com.baffang.user.dto.LoginByCodeDTO;
import com.baffang.user.vo.ProfileVO;
import com.baffang.user.vo.PrivacySettingsVO;
import com.baffang.user.dto.UpdateProfileDTO;
import com.baffang.user.dto.ChangePasswordDTO;
import com.baffang.user.dto.UpdatePrivacySettingsDTO;
import com.baffang.user.dto.ResetPasswordDTO;
import com.baffang.user.mapper.UserMapper;
import com.baffang.user.service.UserService;
import com.baffang.user.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册成功后的用户信息
     */
    @Override
    public RegisterVO register(RegisterDTO registerDTO) {
        // 1. 检查手机号是否已存在
        UserInfo existingUser = userMapper.selectByPhone(registerDTO.getPhone());
        if (existingUser != null) {
            throw new BusinessException(ErrorMsg.PHONE_ALREADY_EXISTS);
        }

        // 2. 校验短信验证码（安全整改：服务端比对，防止恶意批量注册）
        if (!verificationCodeService.verifyCode(registerDTO.getPhone(), registerDTO.getVerifyCode())) {
            throw new BusinessException(ErrorMsg.CAPTCHA_ERROR);
        }

        // 3. 密码加密
        String encryptedPassword = PasswordUtil.encryptPassword(registerDTO.getPassword());

        // 4. 创建用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(registerDTO.getPhone());
        userInfo.setPassword(encryptedPassword);
        // 用户名使用手机号前3位+4个星号+后4位
        String maskedPhone = registerDTO.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        userInfo.setUsername(maskedPhone);
        // 昵称使用相同的手机号脱敏形式
        userInfo.setName(maskedPhone);
        // 初始用户类型为普通用户
        userInfo.setType(UserRoleEnum.NORMAL.getCode());
        // 初始状态
        userInfo.setAuthChargingStation(0); // 未认证充电桩
        userInfo.setAuthVehicle(0); // 未认证车辆
        userInfo.setPrivacyVehicle(0); // 默认不隐藏车辆信息
        userInfo.setPrivacyStation(0); // 默认不隐藏充电桩信息
        userInfo.setPoints(0); // 初始积分0
        userInfo.setBalance(0.0); // 初始余额0
        userInfo.setIsDeleted(0); // 未删除
        userInfo.setCreateTime(new Date());
        userInfo.setUpdateTime(new Date());

        // 5. 保存用户到数据库
        int result = userMapper.insert(userInfo);
        if (result != 1) {
            throw new BusinessException(ErrorMsg.REGISTER_FAILED);
        }

        // 6. 构造并返回注册成功的用户信息
        RegisterVO registerVO = new RegisterVO();
        registerVO.setUserId(userInfo.getId());
        registerVO.setUsername(userInfo.getUsername());
        registerVO.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userInfo.getCreateTime()));

        return registerVO;
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录成功后的用户信息和token
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 确定用于登录的用户名（手机号或用户名）
        String loginIdentifier = loginDTO.getUsername();
        if (loginIdentifier == null || loginIdentifier.isEmpty()) {
            loginIdentifier = loginDTO.getPhone();
        }

        // 2. 创建认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginIdentifier, loginDTO.getPassword());

        // 3. 执行认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 4. 将认证信息存储到SecurityContext中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 获取认证成功的用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // 6. 从数据库中查询用户信息（需要根据实际情况选择查询方式）
        UserInfo userInfo = userMapper.selectByUsername(username);
        // 如果根据用户名查询不到，尝试根据手机号查询
        if (userInfo == null) {
            userInfo = userMapper.selectByPhone(username);
        }

        // 6. 安全检查：确保用户信息不为null
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 7. 根据认证状态确定用户角色
        UserRoleEnum role = UserRoleEnum.fromAuthStatus(
                userInfo.getAuthVehicle(),
                userInfo.getAuthChargingStation());

        // 8. 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID_CLAIM, userInfo.getId());
        claims.put(JwtConstants.USERNAME_CLAIM, userInfo.getUsername());
        claims.put(JwtConstants.AUTH_TYPE_CLAIM, "user"); // 用户端认证类型
        claims.put(JwtConstants.ROLE_CLAIM, role.getCode()); // 角色：根据认证状态确定的用户角色

        String token = JwtUtil.generateToken(
                JwtConstants.SECRET_KEY,
                JwtConstants.USER_TOKEN_EXPIRE_TIME,
                claims);

        // 9. 构造并返回登录成功的用户信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(userInfo.getId());
        loginVO.setUsername(userInfo.getUsername());
        loginVO.setToken(token);

        return loginVO;
    }

    /**
     * 获取用户个人信息
     *
     * @param userId 用户ID
     * @return 用户个人信息
     */
    @Override
    public ProfileVO getProfile(Integer userId) {
        // 1. 根据用户ID查询用户信息
        UserInfo userInfo = userMapper.selectById(userId);

        // 2. 检查用户是否存在
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 3. 构造并返回用户个人信息
        ProfileVO profileVO = new ProfileVO();
        profileVO.setId(userInfo.getId());
        profileVO.setName(userInfo.getName());
        profileVO.setUsername(userInfo.getUsername());
        profileVO.setPhone(userInfo.getPhone());
        profileVO.setEmail(userInfo.getEmail());
        profileVO.setAvatar(userInfo.getAvatar());
        profileVO.setSignature(userInfo.getSignature());
        profileVO.setType(userInfo.getType());
        profileVO.setAuthChargingStation(userInfo.getAuthChargingStation());
        profileVO.setAuthVehicle(userInfo.getAuthVehicle());
        profileVO.setPoints(userInfo.getPoints());
        profileVO.setBalance(userInfo.getBalance());
        profileVO.setCreateTime(userInfo.getCreateTime());

        return profileVO;
    }

    /**
     * 更新用户个人信息
     *
     * @param userId           用户ID
     * @param updateProfileDTO 更新个人信息请求参数
     */
    @Override
    public void updateProfile(Integer userId, UpdateProfileDTO updateProfileDTO) {
        // 1. 检查用户是否存在
        UserInfo existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 创建用户信息对象并设置要更新的字段
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(updateProfileDTO.getName());
        userInfo.setEmail(updateProfileDTO.getEmail());
        userInfo.setAvatar(updateProfileDTO.getAvatar());
        userInfo.setSignature(updateProfileDTO.getSignature());

        // 3. 更新用户信息
        int result = userMapper.updateProfile(userInfo);

        // 4. 检查更新是否成功
        if (result != 1) {
            throw new BusinessException(ErrorMsg.UPDATE_FAILED);
        }
    }

    /**
     * 用户验证码登录
     *
     * @param loginByCodeDTO 验证码登录请求参数
     * @return 登录成功后的用户信息和token
     */
    @Override
    public LoginVO loginByCode(LoginByCodeDTO loginByCodeDTO) {
        // 1. 校验短信验证码（安全整改：服务端比对，一次性使用）
        if (!verificationCodeService.verifyCode(loginByCodeDTO.getPhone(), loginByCodeDTO.getCode())) {
            throw new BusinessException(ErrorMsg.CAPTCHA_ERROR);
        }

        // 2. 根据手机号查询用户信息
        UserInfo userInfo = userMapper.selectByPhone(loginByCodeDTO.getPhone());
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 3. 根据认证状态确定用户角色
        UserRoleEnum role = UserRoleEnum.fromAuthStatus(
                userInfo.getAuthVehicle(),
                userInfo.getAuthChargingStation());

        // 4. 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID_CLAIM, userInfo.getId());
        claims.put(JwtConstants.USERNAME_CLAIM, userInfo.getUsername());
        claims.put(JwtConstants.AUTH_TYPE_CLAIM, "user"); // 用户端认证类型
        claims.put(JwtConstants.ROLE_CLAIM, role.getCode()); // 角色：根据认证状态确定的用户角色

        String token = JwtUtil.generateToken(
                JwtConstants.SECRET_KEY,
                JwtConstants.USER_TOKEN_EXPIRE_TIME,
                claims);

        // 5. 构造并返回登录成功的用户信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(userInfo.getId());
        loginVO.setUsername(userInfo.getUsername());
        loginVO.setToken(token);

        return loginVO;
    }

    /**
     * 修改密码
     *
     * @param userId            用户ID
     * @param changePasswordDTO 修改密码请求参数
     */
    @Override
    public void changePassword(Integer userId, ChangePasswordDTO changePasswordDTO) {
        // 1. 检查用户是否存在
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 验证旧密码是否正确
        if (!PasswordUtil.matchesPassword(changePasswordDTO.getOldPassword(), userInfo.getPassword())) {
            throw new BusinessException(ErrorMsg.PASSWORD_ERROR);
        }

        // 3. 加密新密码
        String encryptedNewPassword = PasswordUtil.encryptPassword(changePasswordDTO.getNewPassword());

        // 4. 更新密码
        int result = userMapper.updatePassword(userId, encryptedNewPassword);
        if (result != 1) {
            throw new BusinessException(ErrorMsg.PASSWORD_EDIT_FAILED);
        }
    }

    @Override
    public PrivacySettingsVO getPrivacySettings(Integer userId) {
        // 1. 检查用户是否存在
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 构造隐私设置VO对象
        PrivacySettingsVO privacySettingsVO = new PrivacySettingsVO();
        // 注意：数据库中privacyVehicle=0表示默认不隐藏（即允许查看），需要转换为1表示允许查看
        privacySettingsVO.setShowVehicle(userInfo.getPrivacyVehicle() == 0 ? 1 : 0);
        // 同理，privacyStation=0表示默认不隐藏（即允许查看），需要转换为1表示允许查看
        privacySettingsVO.setShowChargingStation(userInfo.getPrivacyStation() == 0 ? 1 : 0);

        return privacySettingsVO;
    }

    @Override
    public PrivacySettingsVO updatePrivacySettings(Integer userId, UpdatePrivacySettingsDTO updatePrivacySettingsDTO) {
        // 1. 检查用户是否存在
        UserInfo existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 创建用户信息对象并设置要更新的隐私字段
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);

        // 转换API参数到数据库格式：
        // API: showVehicle=1表示允许查看，0表示不允许查看
        // 数据库: privacyVehicle=0表示不隐藏（允许查看），1表示隐藏（不允许查看）
        userInfo.setPrivacyVehicle(updatePrivacySettingsDTO.getShowVehicle() == 0 ? 1 : 0);
        userInfo.setPrivacyStation(updatePrivacySettingsDTO.getShowChargingStation() == 0 ? 1 : 0);

        // 3. 更新用户隐私设置
        int result = userMapper.updatePrivacySettings(userInfo);
        if (result != 1) {
            throw new BusinessException(ErrorMsg.UPDATE_FAILED);
        }

        // 4. 返回更新后的隐私设置
        return getPrivacySettings(userId);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 1. 检查用户是否存在
        UserInfo userInfo = userMapper.selectByPhone(resetPasswordDTO.getPhone());
        if (userInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 校验短信验证码（安全整改：服务端比对，一次性使用）
        if (!verificationCodeService.verifyCode(resetPasswordDTO.getPhone(), resetPasswordDTO.getCode())) {
            throw new BusinessException(ErrorMsg.CAPTCHA_ERROR);
        }

        // 3. 密码加密
        String encryptedPassword = PasswordUtil.encryptPassword(resetPasswordDTO.getNewpassword());

        // 4. 更新密码
        int result = userMapper.updatePassword(userInfo.getId(), encryptedPassword);
        if (result != 1) {
            throw new BusinessException(ErrorMsg.PASSWORD_EDIT_FAILED);
        }
    }
}
