package com.baffang.common.enums.entityEnum;

/**
 * 对话类型枚举
 * 可用于对话表的type字段
 */
public enum ConversationTypeEnum {
    /**
     * 客服对话
     */
    CUSTOMER_SERVICE(0, "客服对话"),

    /**
     * 预约对话
     */
    RESERVATION(1, "预约对话"),

    /**
     * 紧急呼叫对话
     */
    EMERGENCY_CALL(2, "紧急呼叫对话"),

    /**
     * V2V 对话
     */
    V2V(3, "V2V 对话"),

    /**
     * 普通用户聊天
     */
    USER_CHAT(4, "普通用户聊天");

    private final int code;
    private final String description;

    ConversationTypeEnum(int code, String description) {
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
    public static ConversationTypeEnum fromCode(int code) {
        for (ConversationTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid conversation type code: " + code);
    }
}