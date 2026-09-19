package com.baffang.user.service;

import com.baffang.common.entity.PublicChargeStation;
import com.baffang.user.dto.RandomPublicStationDTO;

import java.util.List;

/**
 * 公桩随机生成服务接口
 */
public interface RandomPublicStationService {
    /**
     * 在指定经纬度范围内随机生成指定数量的公桩
     * @param centerLongitude 中心点经度
     * @param centerLatitude 中心点纬度
     * @param radius 半径（米）
     * @param count 生成数量
     * @return 生成的公桩列表
     */
    List<PublicChargeStation> generatePublicStationsInRadius(double centerLongitude, double centerLatitude, double radius, int count);
    
    /**
     * 在指定区域内随机生成指定数量的公桩
     * @param startLongitude 起始经度
     * @param startLatitude 起始纬度
     * @param endLongitude 结束经度
     * @param endLatitude 结束纬度
     * @param count 生成数量
     * @return 生成的公桩列表
     */
    List<PublicChargeStation> generatePublicStationsInArea(double startLongitude, double startLatitude, double endLongitude, double endLatitude, int count);
    
    /**
     * 根据指定字段生成一个公桩（未指定字段随机生成）
     * @param dto 指定的公桩字段
     * @return 生成的公桩
     */
    PublicChargeStation generateSinglePublicStation(RandomPublicStationDTO dto);
}