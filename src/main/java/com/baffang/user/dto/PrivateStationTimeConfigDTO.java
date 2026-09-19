package com.baffang.user.dto;

import lombok.Data;

/**
 * 私桩时间配置DTO
 */
@Data
public class PrivateStationTimeConfigDTO {
    /**
     * 每日开启时间
     */
    private String openTime;

    /**
     * 每日关闭时间
     */
    private String closeTime;
}