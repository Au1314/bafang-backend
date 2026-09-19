package com.baffang.common.enums.entityEnum;

/**
 * AI对话消息发送者类型枚举
 * 可用于ai_message表的sender_type字段
 */
public enum AIMessageSenderTypeEnum {
    /**
     * 用户
     */
    USER(0, "用户"),

    /**
     * AI
     */
    AI(1, "AI");

    private final int code;
    private final String description;

    AIMessageSenderTypeEnum(int code, String description) {
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
    public static AIMessageSenderTypeEnum fromCode(int code) {
        for (AIMessageSenderTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AI message sender type code: " + code);
    }
}
