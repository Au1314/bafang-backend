package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.constant.PermissionConst;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.enums.AdminPermissionEnum;
import com.baffang.common.enums.AdminRoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员认证服务实现类
 * 实现Spring Security的UserDetailsService接口，用于从数据库加载管理员信息
 */
@Service("adminDetailsService")
public class AdminDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库中根据用户名查询管理员信息
        AdminInfo adminInfo = adminMapper.selectByUsername(username);
        if (adminInfo == null) {
            throw new UsernameNotFoundException("管理员不存在");
        }

        // 2. 根据管理员角色构建权限列表（核心：对齐@PreAuthorize要求的权限标识）
        List<GrantedAuthority> authorities = new ArrayList<>();
        AdminRoleEnum role = AdminRoleEnum.fromCode(adminInfo.getType());
        

        // 3. 核心修复：添加PermissionConst中定义的权限标识（和@PreAuthorize匹配）
        switch (role) {
            case SUPER_ADMIN:
                // 超级管理员添加 ADMIN:ROLE:SUPER_ADMIN 权限（和接口注解一致）
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_SUPER_ADMIN));
                break;
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_COMMON_ADMIN));
                break;
            case CUSTOMER_SERVICE:
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_CUSTOMER_SERVICE));
                break;
            case AUDITOR:
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_AUDITOR));
                break;
            case FORUM_ADMIN:
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_FORUM_ADMIN));
                break;
            case EMERGENCY_RESPONDER:
                authorities.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_EMERGENCY_RESPONDER));
                break;
            default:
                // 未知角色，仅添加基础管理员角色
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // 4. 保留原有逻辑（可选，不影响核心权限校验）
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_" + role.name()));
        // 如果AdminPermissionEnum的权限格式和PermissionConst一致，可保留；否则注释
        // authorities.addAll(AdminPermissionEnum.fromAdminRole(role).getPermissions());

        // 5. 构建UserDetails对象返回（密码会被Spring Security自动校验）
        return new org.springframework.security.core.userdetails.User(
                adminInfo.getUsername(),
                adminInfo.getPassword(),
                // 关键：确保authorities中包含 ADMIN:ROLE:SUPER_ADMIN（超级管理员）
                authorities
        );
    }
}