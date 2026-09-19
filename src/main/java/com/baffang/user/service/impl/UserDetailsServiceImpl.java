package com.baffang.user.service.impl;

import com.baffang.common.entity.UserInfo;
import com.baffang.common.enums.UserPermissionEnum;
import com.baffang.common.enums.UserRoleEnum;
import com.baffang.user.mapper.UserMapper;
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
 * 用户认证服务实现类
 * 实现Spring Security的UserDetailsService接口，用于从数据库加载用户信息
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 首先尝试根据用户名查询用户信息
        UserInfo userInfo = userMapper.selectByUsername(username);

        // 2. 如果没有找到，再尝试根据手机号查询用户信息
        if (userInfo == null) {
            userInfo = userMapper.selectByPhone(username);
        }

        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 根据用户角色分配权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 根据认证车辆和充电桩的状态确定用户角色
        UserRoleEnum role = UserRoleEnum.fromAuthStatus(
                userInfo.getAuthVehicle(),
                userInfo.getAuthChargingStation());
        authorities.add(new SimpleGrantedAuthority("ROLE_USER_" + role.name()));

        // 根据用户角色添加相应的权限
        authorities.addAll(UserPermissionEnum.fromUserRole(role).getPermissions());

        // 构建UserDetails对象返回，将用户信息对象作为principal
        return new org.springframework.security.core.userdetails.User(
                userInfo.getUsername(),
                userInfo.getPassword(),
                authorities);
    }
}
