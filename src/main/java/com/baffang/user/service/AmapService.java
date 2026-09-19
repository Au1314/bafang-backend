package com.baffang.user.service;

import com.alibaba.fastjson2.JSONObject;
import java.util.List;

/**
 * 高德地图服务接口
 */
public interface AmapService {

    /**
     * 计算两点之间的驾车导航距离
     * 
     * @param originLng 起点经度
     * @param originLat 起点纬度
     * @param destLng   终点经度
     * @param destLat   终点纬度
     * @return 距离（米）
     */
    Long getDrivingDistance(Double originLng, Double originLat, Double destLng, Double destLat);

    /**
     * 批量计算驾车导航距离（一个起点到多个终点）
     * 
     * @param originLng    起点经度
     * @param originLat    起点纬度
     * @param destinations 终点列表 [[lng1,lat1], [lng2,lat2], ...]
     * @return 距离列表（米）
     */
    List<Long> getBatchDrivingDistance(Double originLng, Double originLat, List<double[]> destinations);

    /**
     * 批量计算驾车导航距离（优化版本 - 使用高德批量接口）
     * 高德API支持一次计算多个起点到一个终点的距离（最多100个）
     * 如果要计算一个起点到多个终点，可以反过来调用
     * 
     * @param originLng    起点经度
     * @param originLat    起点纬度
     * @param destinations 终点列表 [[lng1,lat1], [lng2,lat2], ...]
     * @return 距离列表（米）
     */
    List<Long> getBatchDrivingDistanceOptimized(Double originLng, Double originLat, List<double[]> destinations);

    /**
     * 驾车路线规划
     * 
     * @param originLng 起点经度
     * @param originLat 起点纬度
     * @param destLng   终点经度
     * @param destLat   终点纬度
     * @param strategy  驾车算路策略
     * @param waypoints 途经点，格式："lng1,lat1;lng2,lat2"
     * @return 路线规划结果
     */
    JSONObject getDrivingRoute(Double originLng, Double originLat, Double destLng, Double destLat,
            Integer strategy, String waypoints);
}