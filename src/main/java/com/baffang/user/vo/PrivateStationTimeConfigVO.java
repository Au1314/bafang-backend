package com.baffang.user.vo;

import lombok.Data;

/**
 * 私桩时间配置VO
 */
@Data
public class PrivateStationTimeConfigVO {
    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 开启状态，0=关闭，1=开启
     */
    private Integer openStatus;

    /**
     * 每日开启时间
     */
    private String openTime;

    /**
     * 每日关闭时间
     */
    private String closeTime;
}