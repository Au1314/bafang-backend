package com.baffang.common.constant;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 管理员类型常量
 */
public class AdminTypeConstants {

    // 身份类型
    public static final int SUPER_ADMIN = 0;       // 超级管理员
    public static final int NORMAL_ADMIN = 1;      // 普通管理员
    public static final int REVIEWER = 2;          // 审核员
    public static final int CUSTOMER_SERVICE = 3;  // 客服
    public static final int EMERGENCY_RESPONDER = 4; // 紧急响应人员
    public static final int FORUM_ADMIN = 5;       // 论坛管理员
    public static final int UNASSIGNED = 10;       // 未分配身份

    /**
     * 超级管理员可设置的身份类型
     */
    public static final Set<Integer> SUPER_ADMIN_CAN_SET = new HashSet<>(
            Arrays.asList(NORMAL_ADMIN, REVIEWER, CUSTOMER_SERVICE, EMERGENCY_RESPONDER, FORUM_ADMIN, UNASSIGNED)
    );

    /**
     * 普通管理员可设置的身份类型
     */
    public static final Set<Integer> NORMAL_ADMIN_CAN_SET = new HashSet<>(
            Arrays.asList(REVIEWER, CUSTOMER_SERVICE, EMERGENCY_RESPONDER, UNASSIGNED)
    );

    /**
     * 获取身份类型名称
     */
    public static String getTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case SUPER_ADMIN: return "超级管理员";
            case NORMAL_ADMIN: return "普通管理员";
            case REVIEWER: return "审核员";
            case CUSTOMER_SERVICE: return "客服";
            case EMERGENCY_RESPONDER: return "紧急响应人员";
            case FORUM_ADMIN: return "论坛管理员";
            case UNASSIGNED: return "未分配身份";
            default: return "未知";
        }
    }

    /**
     * 检查操作者是否有权限设置目标身份
     */
    public static boolean canSetType(Integer operatorType, Integer targetType) {
        if (operatorType == null || targetType == null) {
            return false;
        }
        if (operatorType == SUPER_ADMIN) {
            return SUPER_ADMIN_CAN_SET.contains(targetType);
        }
        if (operatorType == NORMAL_ADMIN) {
            return NORMAL_ADMIN_CAN_SET.contains(targetType);
        }
        return false;
    }

    /**
     * 检查是否有权限修改他人身份（只有超管和普通管理员有权限）
     */
    public static boolean canModifyOthers(Integer operatorType) {
        return operatorType != null && (operatorType == SUPER_ADMIN || operatorType == NORMAL_ADMIN);
    }
}
