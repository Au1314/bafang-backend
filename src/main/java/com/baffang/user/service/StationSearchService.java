package com.baffang.user.service;

import com.baffang.user.dto.StationSearchResultDTO;

import java.util.List;

/**
 * 充电桩搜索服务接口
 */
public interface StationSearchService {
    
    /**
     * 根据经纬度和范围查询周围充电桩
     * 
     * @param longitude 中心经度
     * @param latitude 中心纬度
     * @param radius 搜索半径（米）
     * @param stationType 充电桩类型：0-私桩，1-公桩，2-两者皆有
     * @param sortType 排序规则：1-评分优先，2-距离优先，3-价格优先
     * @return 充电桩列表
     */
    List<StationSearchResultDTO> searchStationsByGeoRange(double longitude, double latitude, double radius, int stationType, int sortType);
}