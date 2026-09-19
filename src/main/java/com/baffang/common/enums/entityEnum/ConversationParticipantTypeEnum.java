package com.baffang.common.enums.entityEnum;

/**
 * 对话参与者类型枚举
 * 可用于对话参与者表的participant_type字段
 */
public enum ConversationParticipantTypeEnum {
    /**
     * 普通用户
     */
    USER(0, "普通用户"),

    /**
     * 管理员
     */
    ADMIN(1, "管理员"),

    /**
     * 系统
     */
    SYSTEM(2, "系统");

    private final int code;
    private final String description;

    ConversationParticipantTypeEnum(int code, String description) {
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
    public static ConversationParticipantTypeEnum fromCode(int code) {
        for (ConversationParticipantTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid conversation participant type code: " + code);
    }
}