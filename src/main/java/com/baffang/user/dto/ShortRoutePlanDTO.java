package com.baffang.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 智能短途路线规划请求参数
 */
@Data
public class ShortRoutePlanDTO {

    /**
     * 当前经度
     */
    private Double currentLng;

    /**
     * 当前纬度
     */
    private Double currentLat;

    /**
     * 当前车辆电量（%）
     */
    private Integer vehicleBattery;

    /**
     * 目标电量（%），默认80
     */
    private Integer targetBattery;

    /**
     * 车辆满电续航（公里），若为空则从用户绑定的车辆中读取参数，再为空则默认200公里
     */
    private Integer vehicleRange;

    /**
     * 可选，0:优先私桩 1：优先公桩（默认公桩）
     */
    private Integer preferStationType;

    /**
     * 偏好类型：1：距离最近 2：价格最低 3：评分最高 4：空位最多
     */
    private Integer preferType;

    /**
     * 充电桩具体参数
     */
    private StationParams stationParams;

    /**
     * 充电桩具体参数内部类
     */
    @Data
    public static class StationParams {

        /**
         * 电流类型：0：直流电，1: 交流电
         */
        @JsonProperty("current_type")
        private Integer currentType;

        /**
         * 最大功率（瓦）
         */
        @JsonProperty("max_charge_power")
        private Integer maxChargePower;
    }
}