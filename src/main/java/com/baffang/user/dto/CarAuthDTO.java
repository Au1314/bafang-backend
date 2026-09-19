package com.baffang.user.dto;

import lombok.Data;

/**
 * 车辆认证请求参数
 */
@Data
public class CarAuthDTO {
    /**
     * 车辆类型
     */
    private String vehicleType;

    /**
     * 用电类型
     */
    private Integer currentType;

    /**
     * 品牌
     */
    private String carBrand;

    /**
     * 型号
     */
    private String carType;

    /**
     * 最大充电电压
     */
    private Integer maxChargeVoltage;

    /**
     * 最大充电电流
     */
    private Integer maxChargeCurrent;

    /**
     * 最大充电功率
     */
    private Integer maxChargePower;

    /**
     * 电池容量
     */
    private Double batteryCapacity;

    /**
     * 放电功率
     */
    private Integer dischargePower;

    /**
     * 续航
     */
    private Integer range;

    /**
     * 车辆号码
     */
    private String vehicleNumber;
}