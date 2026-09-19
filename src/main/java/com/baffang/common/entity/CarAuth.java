package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 车辆认证表实体类
 */
@Data
public class CarAuth {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 认证用户 id(关联用户表 id)
     */
    private Integer ownerId;

    /**
     * 车名
     */
    private String carName;

    /**
     * 品牌
     */
    private String carBrand;

    /**
     * 型号
     */
    private String carType;

    /**
     * 车辆类型
     */
    private String vehicleType;

    /**
     * 车辆号码
     */
    private String vehicleNumber;

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
     * 电池容量，单位：千瓦时
     */
    private Double batteryCapacity;

    /**
     * 放电功率，单位：瓦
     */
    private Integer dischargePower;

    /**
     * 续航，单位：公里
     */
    private Integer range;

    /**
     * 认证状态 0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败
     */
    private Integer authStatus;

    /**
     * 删除状态：0：正常，1：已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}