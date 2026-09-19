package com.baffang.common.enums;

public enum UserRoleEnum {
    NORMAL(0, "普通用户"),
    VIP(1, "VIP用户"),
    VEHICLE_OWNER(2, "认证车主"),
    STATION_OWNER(3, "认证桩主"),
    ALL_OWNER(4, "充电桩与车辆均认证用户");

    private final int code;
    private final String description;

    UserRoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserRoleEnum fromCode(int code) {
        for (UserRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid user role code: " + code);
    }

    /**
     * 根据认证车辆和认证充电桩的状态获取用户角色
     * 
     * @param authVehicle         认证车辆状态（0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败）
     * @param authChargingStation 认证充电桩状态（0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败）
     * @return 用户角色
     */
    public static UserRoleEnum fromAuthStatus(int authVehicle, int authChargingStation) {
        // 已认证车辆和充电桩
        if (authVehicle == 2 && authChargingStation == 2) {
            return ALL_OWNER;
        }
        // 已认证车辆
        if (authVehicle == 2) {
            return VEHICLE_OWNER;
        }
        // 已认证充电桩
        if (authChargingStation == 2) {
            return STATION_OWNER;
        }
        // 默认普通用户
        return NORMAL;
    }
}