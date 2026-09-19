package com.baffang.common.enums;

import com.baffang.common.constant.PermissionConst;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum AdminPermissionEnum {
    SUPER_ADMIN(AdminRoleEnum.SUPER_ADMIN) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            //继承普通管理员的权限
            List<GrantedAuthority> permissions = ADMIN.getPermissions();
            //额外添加超级管理员的权限
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_SUPER_ADMIN));

            return permissions;
        }
    },
    ADMIN(AdminRoleEnum.ADMIN) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();
            
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_COMMON_ADMIN));
            // 管理员还拥有其他所有角色的权限
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_AUDITOR));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_CUSTOMER_SERVICE));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_EMERGENCY_RESPONDER));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_FORUM_ADMIN));
            return permissions;
        }
    },
    AUDITOR(AdminRoleEnum.AUDITOR) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();
          

            // 第二种方式：直接添加角色对应的权限
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_AUDITOR));
            return permissions;
        }
    },
    CUSTOMER_SERVICE(AdminRoleEnum.CUSTOMER_SERVICE) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();
           
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_CUSTOMER_SERVICE));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_AUDITOR));// 客服还拥有审核员的权限
            return permissions;
        }
    },
    EMERGENCY_RESPONDER(AdminRoleEnum.EMERGENCY_RESPONDER) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();

            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_EMERGENCY_RESPONDER));
            return permissions;
        }
    },
    FORUM_ADMIN(AdminRoleEnum.FORUM_ADMIN) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();
            
            permissions.add(new SimpleGrantedAuthority(PermissionConst.ADMIN_FORUM_ADMIN));
            return permissions;
        }
    };

    private final AdminRoleEnum adminRole;

    AdminPermissionEnum(AdminRoleEnum adminRole) {
        this.adminRole = adminRole;
    }

    public AdminRoleEnum getAdminRole() {
        return adminRole;
    }

    public abstract List<GrantedAuthority> getPermissions();

    public static AdminPermissionEnum fromAdminRole(AdminRoleEnum adminRole) {
        for (AdminPermissionEnum permission : values()) {
            if (permission.adminRole == adminRole) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Invalid admin role: " + adminRole);
    }

    public static AdminPermissionEnum fromCode(int code) {
        AdminRoleEnum adminRole = AdminRoleEnum.fromCode(code);
        return fromAdminRole(adminRole);
    }

    /**
     * 根据管理员角色获取所有权限
     * @param adminRole 管理员角色
     * @return 角色的权限集合
     */
    public static List<GrantedAuthority> getPermissionsFromRole(AdminRoleEnum adminRole) {
        AdminPermissionEnum permission = fromAdminRole(adminRole);
        return permission.getPermissions();
    }
}