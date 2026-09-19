package com.baffang.common.enums;

import com.baffang.common.constant.PermissionConst;

import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum UserPermissionEnum {
    NORMAL(UserRoleEnum.NORMAL) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = new ArrayList<>();
            // 第二种认证方式
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_COMMON_USER));
            return permissions;
        }
    },
    VIP(UserRoleEnum.VIP) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = NORMAL.getPermissions();
            return permissions;
        }
    },
    VEHICLE_OWNER(UserRoleEnum.VEHICLE_OWNER) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = NORMAL.getPermissions();

            // 第二种认证方式
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_CAR_OWNER));
            return permissions;
        }
    },
    STATION_OWNER(UserRoleEnum.STATION_OWNER) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = NORMAL.getPermissions();
            
            // 第二种权限管理方式
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_STATION_OWNER));
            return permissions;
        }
    },
    ALL_OWNER(UserRoleEnum.ALL_OWNER) {
        @Override
        public List<GrantedAuthority> getPermissions() {
            List<GrantedAuthority> permissions = NORMAL.getPermissions();
            // 充电桩与车辆均认证用户额外权限（只有第二种权限管理方式）
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_ALL_OWNER));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_STATION_OWNER));
            permissions.add(new SimpleGrantedAuthority(PermissionConst.USER_CAR_OWNER));
            return permissions;
        }
    };

    private final UserRoleEnum userRole;

    UserPermissionEnum(UserRoleEnum userRole) {
        this.userRole = userRole;
    }

    public UserRoleEnum getUserRole() {
        return userRole;
    }

    public abstract List<GrantedAuthority> getPermissions();

    public static UserPermissionEnum fromUserRole(UserRoleEnum userRole) {
        for (UserPermissionEnum permission : values()) {
            if (permission.userRole == userRole) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Invalid user role: " + userRole);
    }

    public static UserPermissionEnum fromCode(int code) {
        UserRoleEnum userRole = UserRoleEnum.fromCode(code);
        return fromUserRole(userRole);
    }
}