package com.baffang.common.enums.entityEnum;

/**
 * V2V呼叫状态枚举
 * 还可用于紧急呼叫
 */
public enum CallStatusEnum {
    /**
     * 待响应
     */
    PENDING_RESPONSE(0, "待响应"),

    /**
     * 已响应
     */
    RESPONDED(1, "已响应"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 已取消
     */
    CANCELED(3, "已取消");

    private final int code;
    private final String description;

    CallStatusEnum(int code, String description) {
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
    public static CallStatusEnum fromCode(int code) {
        for (CallStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid call status code: " + code);
    }
}