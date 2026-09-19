package com.baffang.common.enums.entityEnum;

/**
 * 对话参与者角色枚举
 * 可用于对话参与者表的role字段
 */
public enum ConversationParticipantRoleEnum {
    /**
     * 发起者
     */
    INITIATOR(0, "发起者"),

    /**
     * 响应者
     */
    RESPONDER(1, "响应者"),

    /**
     * 观察者
     */
    OBSERVER(2, "观察者");

    private final int code;
    private final String description;

    ConversationParticipantRoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static ConversationParticipantRoleEnum fromCode(int code) {
        for (ConversationParticipantRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid conversation participant role code: " + code);
    }
}