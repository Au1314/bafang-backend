package com.baffang.common.enums.entityEnum;

import lombok.Getter;

/**
 * 邀请码状态枚举
 * 用于admin_invite_code表的status字段
 */
@Getter
public enum InviteCodeStatusEnum {

    /**
     * 有效
     */
    VALID(0, "有效"),

    /**
     * 已禁用
     */
    DISABLED(1, "已禁用");

    private final Integer code;
    private final String desc;

    InviteCodeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (InviteCodeStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}