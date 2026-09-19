package com.baffang.user.vo;

import lombok.Data;
import java.util.List;

/**
 * 智能短途路线规划返回结果
 */
@Data
public class ShortRoutePlanVO {
    
    /**
     * 推荐充电桩
     */
    private StationVO recommendStation;
    
    /**
     * 备选充电桩列表
     */
    private List<StationVO> alternativeStations;
    
    /**
     * 充电桩信息内部类
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