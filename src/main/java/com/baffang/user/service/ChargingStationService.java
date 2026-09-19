package com.baffang.user.service;

import com.baffang.user.dto.ChargingStationRequest;
import com.baffang.user.vo.ChargingStationVO;
import java.util.List;

public interface ChargingStationService {
    
    /**
     * 根据经纬度搜索附近的充电站
     */
    List<ChargingStationVO> findNearbyStations(ChargingStationRequest request);
}