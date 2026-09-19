package com.baffang.common.enums;

public enum AuthTypeEnum {
    USER("user", "用户端"),
    ADMIN("admin", "管理端");

    private final String code;
    private final String description;

    AuthTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuthTypeEnum fromCode(String code) {
        for (AuthTypeEnum authType : values()) {
            if (authType.code.equals(code)) {
                return authType;
            }
        }
        throw new IllegalArgumentException("Invalid auth type code: " + code);
    }
}