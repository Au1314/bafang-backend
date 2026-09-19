package com.baffang.user.dto;

import lombok.Data;

/**
 * 长途路径规划请求DTO
 * 
 * @author 
 * @date 2026-01-12
 */
@Data
public class LongRoutePlanDTO {
    // 起点
    private Double startLng;
    private Double startLat;
    
    // 终点
    private Double endLng;
    private Double endLat;
    
    // 车辆参数
    private Integer vehicleRange; // 满电续航（公里）
    private Integer currentBattery; // 当前电量（%）
    private Integer bottomLineBattery; // 电量底线（%）
    
    // 用户偏好
    private Integer preferStationType; // 0-私桩优先，1-公桩优先
    private Integer preferredCurrentType; // 充电类型：0-直流，1-交流
    private Integer minChargePower; // 最小充电功率（W）
    private Integer routeType; // 路线类型：1-最快，2-最短，3-充电桩最优
    private Integer preferType; // 充电桩排序偏好：1-距离最近，2-充电最快，3-价格最低，4-评分最高
}
