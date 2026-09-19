package com.baffang.user.service.impl;

import com.baffang.common.client.AmapClient;
import com.baffang.user.dto.ChargingStationRequest;
import com.baffang.user.service.ChargingStationService;
import com.baffang.user.vo.ChargingStationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ChargingStationServiceImpl implements ChargingStationService {
    
    @Resource
    private AmapClient amapClient;
    
    @Override
    public List<ChargingStationVO> findNearbyStations(ChargingStationRequest request) {
        log.info("搜索附近充电站: 经度={}, 纬度={}, 半径={}米", 
                request.getLongitude(), request.getLatitude(), request.getRadius());
        
        // 参数校验和默认值设置
        Integer radius = request.getRadius();
        if (radius == null || radius <= 0) {
            radius = 5000;
        }
        
        Integer page = request.getPage();
        if (page == null || page <= 0) {
            page = 1;
        }
        
        Integer pageSize = request.getPageSize();
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        
        return amapClient.searchChargingStations(
                request.getLongitude(),
                request.getLatitude(),
                radius,
                page,
                pageSize
        );
    }
}