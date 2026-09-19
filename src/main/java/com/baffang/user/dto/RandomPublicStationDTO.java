package com.baffang.user.dto;

import lombok.Data;

/**
 * 随机生成公桩请求参数DTO
 */
@Data
public class RandomPublicStationDTO {
    /**
     * 公桩名称
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
     * 电流类型：0：直流电，1：交流电
     */
    private Integer currentType;
    
    /**
     * 充电价格，单位：元/度
     */
    private Double price;
    
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
     * 当前车辆数
     */
    private Integer currentVehicles;
    
    /**
     * 开放状态：0：关闭，1：开启
     */
    private Integer openStatus;
    
    /**
     * 公桩建设者
     */
    private String provider;
    
    /**
     * 演示图片
     */
    private String image;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 评分
     */
    private Double rating;
}