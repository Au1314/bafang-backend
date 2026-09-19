package com.baffang.user.dto;

import lombok.Data;

/**
 * 新增私桩认证请求参数
 */
@Data
public class PrivateStationAuthDTO {
    /**
     * 私桩名称
     */
    private String name;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 基础充电价格（元/度）
     */
    private Double price;

    /**
     * 私桩类型：0：普通私桩，1：专业私桩
     */
    private Integer type;

    /**
     * 电流类型：0：直流电，1：交流电
     */
    private Integer currentType;

    /**
     * 最大充电电压，单位：伏特
     */
    private Integer maxChargeVoltage;

    /**
     * 最大充电电流，单位：安培
     */
    private Integer maxChargeCurrent;

    /**
     * 最大充电功率，单位：瓦
     */
    private Integer maxChargePower;

    /**
     * 最大车辆数
     */
    private Integer maxVehicles;

    /**
     * 认证附件URL（多个URL用逗号分隔）
     */
    private String image;

    /**
     * 私桩描述
     */
    private String description;
}