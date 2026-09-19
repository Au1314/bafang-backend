package com.baffang.common.enums.entityEnum;

/**
 * 对话状态枚举
 * 可用于对话表的status字段
 */
public enum ConversationStatusEnum {
    /**
     * 进行中
     */
    IN_PROGRESS(1, "进行中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 已关闭
     */
    CLOSED(3, "已关闭");

    private final int code;
    private final String description;

    ConversationStatusEnum(int code, String description) {
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
    public static ConversationStatusEnum fromCode(int code) {
        for (ConversationStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid conversation status code: " + code);
    }
}