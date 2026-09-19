package com.baffang.common.enums.entityEnum;

/**
 * AI对话状态枚举
 * 可用于ai_conversation表的status字段
 */
public enum AIChatStatusEnum {
    /**
     * 进行中
     */
    IN_PROGRESS(0, "进行中"),

    /**
     * 已结束
     */
    COMPLETED(1, "已结束"),

    /**
     * 已中断
     */
    INTERRUPTED(2, "已中断");

    private final int code;
    private final String description;

    AIChatStatusEnum(int code, String description) {
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
    public static AIChatStatusEnum fromCode(int code) {
        for (AIChatStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AI chat status code: " + code);
    }
}
