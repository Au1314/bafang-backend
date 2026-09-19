package com.baffang.common.constant;

/**
 * 权限枚举常量
 * 直接枚举角色对应的权限，进行粗粒度控制
 */
public class PermissionConst {

    // ************************直接枚举角色来代表对应权限****************** */
    // 三个字段分别代表 管理端/用户端：规则（固定为ROLE）：角色
    // 管理端
    public static final String ADMIN_SUPER_ADMIN = "ADMIN:ROLE:SUPER_ADMIN"; // 超级管理员权限
    public static final String ADMIN_COMMON_ADMIN = "ADMIN:ROLE:ADMIN"; // 普通用户权限
    public static final String ADMIN_CUSTOMER_SERVICE = "ADMIN:ROLE:CUSTOMER_SERVICE"; // 客户服务权限
    public static final String ADMIN_AUDITOR = "ADMIN:ROLE:AUDITOR"; // 审核员
    public static final String ADMIN_FORUM_ADMIN = "ADMIN:ROLE:FORUM_ADMIN"; // 论坛管理员权限
    public static final String ADMIN_EMERGENCY_RESPONDER = "ADMIN:ROLE:EMERGENCY_RESPONDER"; // 紧急响应人员

    // 用户端
    public static final String USER_COMMON_USER = "USER:ROLE:COMMON_USER"; // 普通用户权限
    public static final String USER_CAR_OWNER = "USER:ROLE:CAR_OWNER"; // 车主权限
    public static final String USER_STATION_OWNER = "USER:ROLE:STATION_OWNER"; // 充电站所有者
    public static final String USER_ALL_OWNER = "USER:ROLE:ALL_OWNER"; // 充电站与车辆所有者
    // VIP暂不实装
    public static final String USER_VIP = "USER:ROLE:VIP"; // vip用户权限

}
