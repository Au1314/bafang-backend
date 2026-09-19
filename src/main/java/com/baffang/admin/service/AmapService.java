package com.baffang.admin.service;

import java.util.List;

/**
 * 高德地图服务接口
 */
public interface AmapService {

    /**
     * 计算两点之间的驾车导航距离
     * @param originLng 起点经度
     * @param originLat 起点纬度
     * @param destLng 终点经度
     * @param destLat 终点纬度
     * @return 距离（米）
     */
    Long getDrivingDistance(Double originLng, Double originLat, Double destLng, Double destLat);

    /**
     * 批量计算驾车导航距离（一个起点到多个终点）
     * @param originLng 起点经度
     * @param originLat 起点纬度
     * @param destinations 终点列表 [[lng1,lat1], [lng2,lat2], ...]
     * @return 距离列表（米）
     */
    List<Long> getBatchDrivingDistance(Double originLng, Double originLat, List<double[]> destinations);
}
