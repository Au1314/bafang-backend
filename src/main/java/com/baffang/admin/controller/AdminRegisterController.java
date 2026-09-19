package com.baffang.admin.controller;

import com.baffang.admin.dto.AdminRegisterDTO;
import com.baffang.admin.dto.AdminRegisterResultDTO;
import com.baffang.admin.dto.InviteCodeDTO;
import com.baffang.admin.dto.InviteCodeGenerateDTO;
import com.baffang.admin.mapper.AdminInviteCodeMapper;
import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.constant.AuthConstants;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.AdminInviteCode;
import com.baffang.common.enums.AdminRoleEnum;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.common.util.PasswordUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员注册控制器
 */
@Slf4j
@RestController
public class AdminRegisterController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminInviteCodeMapper inviteCodeMapper;

    /**
     * 管理员注册（需要邀请码）
     */
    @PostMapping(AuthConstants.ADMIN_REGISTER_URL)
    public Result<AdminRegisterResultDTO> register(@RequestBody @Validated AdminRegisterDTO registerDTO) {
        // 1. 校验两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException(ErrorMsg.PASSWORD_NOT_MATCH);
        }

        // 2. 校验邀请码
        AdminInviteCode inviteCode = inviteCodeMapper.selectByCode(registerDTO.getInviteCode());
        if (inviteCode == null) {
            throw new BusinessException(ErrorMsg.INVITE_CODE_INVALID);
        }

        // 检查是否过期
        if (inviteCode.getExpireTime() != null && inviteCode.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorMsg.INVITE_CODE_EXPIRED);
        }

        // 检查使用次数
        if (inviteCode.getUsedCount() >= inviteCode.getMaxUses()) {
            throw new BusinessException(ErrorMsg.INVITE_CODE_USED_UP);
        }

        // 3. 校验用户名是否已存在
        AdminInfo existAdmin = adminMapper.selectByUsername(registerDTO.getUsername());
        if (existAdmin != null) {
            throw new BusinessException(ErrorMsg.USERNAME_EXISTS);
        }

        // 4. 创建管理员对象
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setUsername(registerDTO.getUsername());
        adminInfo.setName(registerDTO.getName());
        adminInfo.setPassword(PasswordUtil.encryptPassword(registerDTO.getPassword()));
        adminInfo.setAvatar(registerDTO.getAvatar());
        adminInfo.setType(inviteCode.getTargetType());
        adminInfo.setIsDeleted(0);
        adminInfo.setCreateTime(new Date());
        adminInfo.setUpdateTime(new Date());

        // 5. 保存到数据库
        adminMapper.insert(adminInfo);

        // 6. 更新邀请码使用次数
        inviteCodeMapper.incrementUsedCount(inviteCode.getId());

        // 7. 构建返回结果
        AdminRegisterResultDTO resultDTO = new AdminRegisterResultDTO();
        BeanUtils.copyProperties(adminInfo, resultDTO);
        resultDTO.setTypeName(getTypeName(adminInfo.getType()));

        log.info("管理员 {} 使用邀请码 {} 注册成功，角色: {}", 
                registerDTO.getUsername(), registerDTO.getInviteCode(), resultDTO.getTypeName());

        return Result.success(resultDTO);
    }

    /**
     * 生成邀请码（仅超级管理员）
     */
    @PostMapping(AuthConstants.ADMIN_INVITE_CODE_GENERATE_URL)
    @PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
    public Result<InviteCodeDTO> generateInviteCode(
            @RequestBody @Validated InviteCodeGenerateDTO generateDTO,
            HttpServletRequest request) {

        // 1. 获取当前管理员信息
        Integer adminId = getAdminIdFromRequest(request);
        AdminInfo currentAdmin = adminMapper.selectById(Long.valueOf(adminId));

        if (currentAdmin == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 再次校验是否为超级管理员
        if (currentAdmin.getType() != 0) {
            throw new BusinessException(ErrorMsg.NOT_SUPER_ADMIN);
        }

        // 3. 不允许生成超级管理员邀请码
        if (generateDTO.getTargetType() == 0) {
            throw new BusinessException(ErrorMsg.CANNOT_CREATE_SUPER_ADMIN);
        }

        // 4. 生成随机邀请码
        String code = generateRandomCode(8);

        // 5. 创建邀请码对象
        AdminInviteCode inviteCode = new AdminInviteCode();
        inviteCode.setCode(code);
        inviteCode.setCreatedBy(adminId);
        inviteCode.setTargetType(generateDTO.getTargetType());
        inviteCode.setMaxUses(generateDTO.getMaxUses());
        inviteCode.setUsedCount(0);
        inviteCode.setStatus(0);
        inviteCode.setCreateTime(new Date());

        // 设置过期时间
        if (generateDTO.getExpireHours() != null && generateDTO.getExpireHours() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, generateDTO.getExpireHours());
            inviteCode.setExpireTime(calendar.getTime());
        }

        // 6. 保存到数据库
        inviteCodeMapper.insert(inviteCode);

        // 7. 构建返回结果
        InviteCodeDTO inviteCodeDTO = new InviteCodeDTO();
        BeanUtils.copyProperties(inviteCode, inviteCodeDTO);
        inviteCodeDTO.setTargetTypeName(getTypeName(inviteCode.getTargetType()));

        log.info("超级管理员 {} 生成邀请码 {}，目标角色: {}", 
                currentAdmin.getUsername(), code, inviteCodeDTO.getTargetTypeName());

        return Result.success(inviteCodeDTO);
    }

    /**
 * 查询我创建的邀请码
 */
@GetMapping(AuthConstants.ADMIN_INVITE_CODE_LIST_URL)
@PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
public Result<List<InviteCodeDTO>> getMyInviteCodes(HttpServletRequest request) {
    Integer adminId = getAdminIdFromRequest(request);
    List<AdminInviteCode> inviteCodes = inviteCodeMapper.selectByCreatedBy(adminId);

    List<InviteCodeDTO> result = inviteCodes.stream().map(code -> {
        InviteCodeDTO dto = new InviteCodeDTO();
        BeanUtils.copyProperties(code, dto);
        dto.setTargetTypeName(getTypeName(code.getTargetType()));
        dto.setStatusName(getStatusName(code.getStatus()));
        return dto;
    }).collect(Collectors.toList());

    return Result.success(result);
}

/**
 * 获取状态名称
 */
private String getStatusName(Integer status) {
    if (status == null) return "未知";
    return status == 0 ? "正常" : "禁用";
}


    /**
 * 删除邀请码（仅超级管理员，可删除任何邀请码）
 */
@DeleteMapping(AuthConstants.ADMIN_INVITE_CODE_DELETE_URL)
@PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
public Result<Void> deleteInviteCode(
        @PathVariable Integer codeId,
        HttpServletRequest request) {

    // 1. 获取当前管理员信息
    Integer adminId = getAdminIdFromRequest(request);
    AdminInfo currentAdmin = adminMapper.selectById(Long.valueOf(adminId));

    if (currentAdmin == null) {
        throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
    }

    // 2. 查询邀请码
    AdminInviteCode inviteCode = inviteCodeMapper.selectById(codeId);
    if (inviteCode == null) {
        throw new BusinessException(ErrorMsg.INVITE_CODE_NOT_FOUND);
    }

    // 3. 超级管理员可以删除任何邀请码（已通过 @PreAuthorize 校验）
    inviteCodeMapper.deleteById(codeId);

    log.info("超级管理员 {} 删除邀请码 {}（创建者ID: {}）", 
            currentAdmin.getUsername(), inviteCode.getCode(), inviteCode.getCreatedBy());

    return Result.success(null);
}

/**
 * 批量删除邀请码（超级管理员可删除任何邀请码）
 */
@DeleteMapping("/api/v1/admin/invite-code/batch")
@PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
public Result<Void> batchDeleteInviteCodes(
        @RequestBody List<Integer> codeIds,
        HttpServletRequest request) {

    if (codeIds == null || codeIds.isEmpty()) {
        throw new BusinessException(ErrorMsg.PARAM_ERROR);
    }

    Integer adminId = getAdminIdFromRequest(request);
    AdminInfo currentAdmin = adminMapper.selectById(Long.valueOf(adminId));

    int deleteCount = 0;
    for (Integer codeId : codeIds) {
        AdminInviteCode inviteCode = inviteCodeMapper.selectById(codeId);
        if (inviteCode != null) {
            inviteCodeMapper.deleteById(codeId);
            deleteCount++;
        }
    }

    log.info("超级管理员 {} 批量删除邀请码，成功删除: {} 个", 
            currentAdmin != null ? currentAdmin.getUsername() : adminId, deleteCount);

    return Result.success(null);
}

/**
 * 禁用/启用邀请码（超级管理员可修改任何邀请码）
 */
@PutMapping("/api/v1/admin/invite-code/{codeId}/status")
@PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
public Result<Void> updateInviteCodeStatus(
        @PathVariable Integer codeId,
        @RequestParam Integer status,
        HttpServletRequest request) {

    // status: 0-正常, 1-禁用
    if (status == null || (status != 0 && status != 1)) {
        throw new BusinessException(ErrorMsg.PARAM_ERROR);
    }

    Integer adminId = getAdminIdFromRequest(request);
    AdminInfo currentAdmin = adminMapper.selectById(Long.valueOf(adminId));

    AdminInviteCode inviteCode = inviteCodeMapper.selectById(codeId);
    if (inviteCode == null) {
        throw new BusinessException(ErrorMsg.INVITE_CODE_NOT_FOUND);
    }

    // 超级管理员可以修改任何邀请码状态
    inviteCodeMapper.updateStatus(codeId, status);

    log.info("超级管理员 {} {} 邀请码 {}（创建者ID: {}）", 
            currentAdmin != null ? currentAdmin.getUsername() : adminId, 
            status == 1 ? "禁用" : "启用", 
            inviteCode.getCode(),
            inviteCode.getCreatedBy());

    return Result.success(null);
}


    /**
 * 从请求头中获取管理员ID
 */
private Integer getAdminIdFromRequest(HttpServletRequest request) {
    String token = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
    if (token != null && token.startsWith(JwtConstants.TOKEN_PREFIX)) {
        token = token.substring(JwtConstants.TOKEN_PREFIX.length()).trim();
        
        try {
            Claims claims = JwtUtil.parseToken(JwtConstants.SECRET_KEY, token);
            
            // 优先尝试新的 adminId，兼容旧的 userId
            Integer adminId = claims.get("adminId", Integer.class);
            if (adminId != null) {
                return adminId;
            }
            
            // 兼容旧 Token
            Long userId = claims.get(JwtConstants.USER_ID_CLAIM, Long.class);
            if (userId != null) {
                return userId.intValue();
            }
            
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        }
    }
    throw new BusinessException(ErrorMsg.USER_NOT_LOGIN);
}


    /**
     * 生成随机码
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 获取角色类型名称
     */
    private String getTypeName(Integer type) {
        if (type == null) return "未知";
        try {
            return AdminRoleEnum.fromCode(type).getDescription();
        } catch (Exception e) {
            return "未知";
        }
    }
}
