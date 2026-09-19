package com.baffang.admin.service.impl;

import com.baffang.admin.dto.AdminRegisterDTO;
import com.baffang.admin.dto.InviteCodeGenerateDTO;
import com.baffang.admin.mapper.AdminInviteCodeMapper;
import com.baffang.admin.mapper.AdminMapper;
import com.baffang.admin.service.AdminAuthService;
import com.baffang.admin.vo.AdminRegisterVO;
import com.baffang.admin.vo.InviteCodeVO;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.AdminInviteCode;
import com.baffang.common.enums.AdminRoleEnum;
import com.baffang.common.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminInviteCodeMapper inviteCodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminRegisterVO register(AdminRegisterDTO registerDTO) {
        // 1. 校验两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 2. 校验邀请码
        AdminInviteCode inviteCode = inviteCodeMapper.selectByCode(registerDTO.getInviteCode());
        if (inviteCode == null) {
            throw new RuntimeException("邀请码无效");
        }

        // 检查是否过期
        if (inviteCode.getExpireTime() != null && inviteCode.getExpireTime().before(new Date())) {
            throw new RuntimeException("邀请码已过期");
        }

        // 检查使用次数
        if (inviteCode.getUsedCount() >= inviteCode.getMaxUses()) {
            throw new RuntimeException("邀请码已被使用完毕");
        }

        // 3. 校验用户名是否已存在
        AdminInfo existAdmin = adminMapper.selectByUsername(registerDTO.getUsername());
        if (existAdmin != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 4. 创建管理员对象
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setUsername(registerDTO.getUsername());
        adminInfo.setName(registerDTO.getName());
        // 密码加密存储
        adminInfo.setPassword(PasswordUtil.encryptPassword(registerDTO.getPassword()));
        adminInfo.setAvatar(registerDTO.getAvatar());
        // 使用邀请码指定的角色类型
        adminInfo.setType(inviteCode.getTargetType());
        adminInfo.setIsDeleted(0);
        adminInfo.setCreateTime(new Date());
        adminInfo.setUpdateTime(new Date());

        // 5. 保存到数据库
        adminMapper.insert(adminInfo);

        // 6. 更新邀请码使用次数
        inviteCodeMapper.incrementUsedCount(inviteCode.getId());

        // 7. 构建返回VO
        AdminRegisterVO registerVO = new AdminRegisterVO();
        BeanUtils.copyProperties(adminInfo, registerVO);
        registerVO.setTypeName(getTypeName(adminInfo.getType()));

        log.info("管理员 {} 使用邀请码 {} 注册成功，角色类型: {}", 
                registerDTO.getUsername(), registerDTO.getInviteCode(), registerVO.getTypeName());
        
        return registerVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InviteCodeVO generateInviteCode(Integer adminId, InviteCodeGenerateDTO generateDTO) {
        // 1. 校验当前管理员是否为超级管理员
        AdminInfo currentAdmin = adminMapper.selectById(Long.valueOf(adminId));
        if (currentAdmin == null) {
            throw new RuntimeException("管理员不存在");
        }
        
        if (currentAdmin.getType() != 0) {
            throw new RuntimeException("只有超级管理员可以生成邀请码");
        }

        // 2. 不允许生成超级管理员邀请码
        if (generateDTO.getTargetType() == 0) {
            throw new RuntimeException("不允许通过邀请码创建超级管理员");
        }

        // 3. 生成随机邀请码（8位大写字母+数字）
        String code = generateRandomCode(8);

        // 4. 创建邀请码对象
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

        // 5. 保存到数据库
        inviteCodeMapper.insert(inviteCode);

        // 6. 构建返回VO
        InviteCodeVO inviteCodeVO = new InviteCodeVO();
        BeanUtils.copyProperties(inviteCode, inviteCodeVO);
        inviteCodeVO.setTargetTypeName(getTypeName(inviteCode.getTargetType()));

        log.info("超级管理员 {} 生成邀请码 {}，目标角色: {}", 
                currentAdmin.getUsername(), code, inviteCodeVO.getTargetTypeName());
        
        return inviteCodeVO;
    }

    @Override
    public List<InviteCodeVO> getMyInviteCodes(Integer adminId) {
        List<AdminInviteCode> inviteCodes = inviteCodeMapper.selectByCreatedBy(adminId);
        
        return inviteCodes.stream().map(code -> {
            InviteCodeVO vo = new InviteCodeVO();
            BeanUtils.copyProperties(code, vo);
            vo.setTargetTypeName(getTypeName(code.getTargetType()));
            return vo;
        }).collect(Collectors.toList());
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
