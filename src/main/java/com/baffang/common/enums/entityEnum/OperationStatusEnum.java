package com.baffang.common.enums.entityEnum;

import lombok.Getter;

/**
 * 操作状态枚举
 * 用于系统日志表的status字段
 */
@Getter
public enum OperationStatusEnum {

    /**
     * 操作失败
     */
    FAIL(0, "失败"),

    /**
     * 操作成功
     */
    SUCCESS(1, "成功");

    private final Integer code;
    private final String message;

    OperationStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取枚举
     * 
     * @param code 状态码
     * @return 操作状态枚举
     */
    public static OperationStatusEnum getByCode(Integer code) {
        for (OperationStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}