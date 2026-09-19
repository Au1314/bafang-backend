package com.baffang.user.vo;

import lombok.Data;
import java.util.List;

/**
 * 长途路径规划返回结果 2.0
 */
@Data
public class LongRoutePlanVO20 {

    /**
     * 规划结果消息
     */
    private String message;

    /**
     * 总距离（公里）
     */
    private Double totalDistance;

    /**
     * 预计总时间（小时）
     */
    private Double estimatedTime;

    /**
     * 充电停靠点列表
     */
    private List<ChargingStop> chargingStops;

    /**
     * 总请求数
     */
    private long totalRequests;

    /**
     * 每秒请求数
     */
    private long requestsPerSecond;

    /**
     * 充电停靠点信息
     */
    @Data
    public static class ChargingStop {

        /**
         * 停靠点标号
         */
        private Integer stopNumber;

        /**
         * 充电桩信息
         */
        private StationVO station;

        /**
         * 到达时电量（%）
         */
        private Double arrivalBattery;

        /**
         * 需要充电量（%）
         */
        private Double chargingAmount;

        /**
         * 预计充电时间（分钟）
         */
        private Integer estimatedChargingTime;

        /**
         * 距上一充电点距离（公里）
         */
        private Double distanceFromPrev;

        /**
         * 距终点剩余距离（公里）
         */
        private Double distanceToEnd;

        /**
         * 附近备选充电桩
         */
        private List<StationVO> alternativeStations;
    }

    /**
     * 充电桩信息
     */
    @Data
    public static class StationVO {

        /**
         * 充电桩ID
         */
        private Integer stationId;

        /**
         * 充电桩名称
         */
        private String stationName;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 纬度
         */
        private Double latitude;

        /**
         * 地址
         */
        private String address;

        /**
         * 距离（米）
         */
        private Integer distance;

        /**
         * 桩类型：0-私桩，1-公桩
         */
        private Integer stationType;

        /**
         * 电流类型：0-直流电，1-交流电
         */
        private Integer currentType;

        /**
         * 最大功率（瓦）
         */
        private Integer maxChargePower;

        /**
         * 价格（元/度）
         */
        private Double price;

        /**
         * 评分
         */
        private Double rating;

        /**
         * 可用充电桩数量
         */
        private Integer availableCount;

        /**
         * 预计充电时间（分钟）
         */
        private Integer estimatedChargeTime;

        /**
         * 预计到达时间（分钟）
         */
        private Integer estimatedArrivalTime;
    }
}