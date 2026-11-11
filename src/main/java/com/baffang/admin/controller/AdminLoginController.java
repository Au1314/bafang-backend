package com.baffang.admin.controller;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.constant.AuthConstants;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import java.util.Date;


import jakarta.servlet.http.HttpServletRequest;

import com.baffang.admin.dto.LoginDTO;
import com.baffang.admin.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录控制器
 */
@RestController
public class AdminLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService adminDetailsService;

    @Autowired
    private AdminMapper adminMapper;

    // 管理员类型描述映射
    private static final String[] TYPE_DESCS = {"超级管理员", "普通管理员", "审核员", "客服", "紧急响应人员", "论坛管理员"};

    /**
     * 管理员登录
     * 
     * @param loginDTO 登录请求参数
     * @return 登录结果
     */
    @PostMapping(AuthConstants.ADMIN_LOGIN_URL)
    @com.baffang.common.ratelimit.RateLimit(limit = 10, windowSeconds = 60) // 防爆破：60秒10次/IP
    public Result<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        // 创建认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // 执行认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 将认证信息存储到SecurityContext中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取认证成功的管理员信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // 从数据库中查询管理员信息
        AdminInfo adminInfo = adminMapper.selectByUsername(username);

        // 安全检查：确保管理员信息不为null
        if (adminInfo == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 更新最后登录时间
        adminMapper.updateLastLoginTime(adminInfo.getId());

        // 过期时间（秒）
        long expiresInSeconds = JwtConstants.ADMIN_TOKEN_EXPIRE_TIME / 1000;

        // 生成JWT令牌，包含所需字段
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminInfo.getId());
        claims.put("type", adminInfo.getType());
        claims.put(JwtConstants.USERNAME_CLAIM, adminInfo.getUsername());
        claims.put(JwtConstants.AUTH_TYPE_CLAIM, "admin");
        System.out.println("=== 当前服务器时间: " + new Date() + " ===");
        String token = JwtUtil.generateToken(
                JwtConstants.SECRET_KEY,
                JwtConstants.ADMIN_TOKEN_EXPIRE_TIME,
                claims);

        // 构建管理员信息DTO
        TokenDTO.AdminInfoDTO adminInfoDTO = new TokenDTO.AdminInfoDTO();
        adminInfoDTO.setId(adminInfo.getId());
        adminInfoDTO.setUsername(adminInfo.getUsername());
        adminInfoDTO.setName(adminInfo.getName());
        adminInfoDTO.setCreateTime(adminInfo.getCreateTime());
        adminInfoDTO.setUpdateTime(adminInfo.getUpdateTime());
        adminInfoDTO.setType(adminInfo.getType());
        adminInfoDTO.setTypeDesc(getTypeDesc(adminInfo.getType()));

        // 构建返回结果
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken(token);
        tokenDTO.setTokenType("Bearer");
        tokenDTO.setExpiresIn(expiresInSeconds);
        tokenDTO.setAdminInfo(adminInfoDTO);

        return Result.success(tokenDTO);
    }
    
    @PostMapping(AuthConstants.ADMIN_LOGOUT_URL)
    public Result<Void> logout(HttpServletRequest request) {
        // 清除 SecurityContext 中的认证信息
        SecurityContextHolder.clearContext();
        
        return Result.success(null);
    }

    /**
     * 获取管理员类型描述
     */
    private String getTypeDesc(Integer type) {
        if (type == null || type < 0 || type >= TYPE_DESCS.length) {
            return "未知";
        }
        return TYPE_DESCS[type];
    }
}
