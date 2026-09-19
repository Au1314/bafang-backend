package com.baffang.common.enums.entityEnum;

/**
 * 对话消息类型枚举
 * 可用于对话消息表的message_type字段
 * 可用于AI对话消息表(ai_message)的message_type字段
 */
public enum ConversationMessageTypeEnum {
    /**
     * 文本
     */
    TEXT(0, "文本"),

    /**
     * 图片
     */
    IMAGE(1, "图片"),

    /**
     * 语音
     */
    VOICE(2, "语音"),

    /**
     * 系统通知
     */
    SYSTEM_NOTIFICATION(3, "系统通知");

    private final int code;
    private final String description;

    ConversationMessageTypeEnum(int code, String description) {
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
    public static ConversationMessageTypeEnum fromCode(int code) {
        for (ConversationMessageTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid conversation message type code: " + code);
    }
}