package com.baffang.common.enums;

public enum AdminRoleEnum {
    SUPER_ADMIN(0, "超级管理员"),
    ADMIN(1, "普通管理员"),
    AUDITOR(2, "审核员"),
    CUSTOMER_SERVICE(3, "客服"),
    EMERGENCY_RESPONDER(4, "紧急响应人员"),
    FORUM_ADMIN(5, "论坛管理员"),
    UNASSIGNED(10, "未分配身份");

    private final int code;
    private final String description;

    AdminRoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AdminRoleEnum fromCode(int code) {
        for (AdminRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid admin role code: " + code);
    }
}