package com.baffang.user.service.impl;

import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.PublicChargeStation;
import com.baffang.common.util.GeoUtils;
import com.baffang.user.dto.StationSearchResultDTO;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.StationSearchService;
import com.baffang.user.vo.NaviPrivateStationVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 充电桩搜索服务实现类
 */
@Service
public class StationSearchServiceImpl implements StationSearchService {

    @Resource
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    @Resource
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    // 默认搜索范围（经纬度）
    private static final double DEFAULT_LNG_LAT_RANGE = 0.3;

    @Override
    public List<StationSearchResultDTO> searchStationsByGeoRange(double longitude, double latitude,
            double radius, int stationType, int sortType) {
        // 1. 计算查询的经纬度范围
        double minLng = longitude - DEFAULT_LNG_LAT_RANGE;
        double maxLng = longitude + DEFAULT_LNG_LAT_RANGE;
        double minLat = latitude - DEFAULT_LNG_LAT_RANGE;
        double maxLat = latitude + DEFAULT_LNG_LAT_RANGE;

        // 2. 查询符合条件的充电桩
        List<StationSearchResultDTO> allStations = new ArrayList<>();

        // 查询私桩
        if (stationType == 0 || stationType == 2) {
            List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                    .selectPrivateStationsByGeoRange(null, minLng, maxLng, minLat, maxLat);

            if (privateStationList != null && !privateStationList.isEmpty()) {
                for (NaviPrivateStationVO privateStation : privateStationList) {
                    // 根据ID查询完整的私桩信息
                    PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper
                            .selectPrivateStationById(privateStation.getId());
                    if (privateChargeStation != null) {
                        StationSearchResultDTO dto = new StationSearchResultDTO();
                        dto.setId(privateChargeStation.getId());
                        dto.setName(privateChargeStation.getName());
                        dto.setLocationName(privateChargeStation.getLocationName());
                        dto.setLongitude(privateChargeStation.getLongitude().doubleValue());
                        dto.setLatitude(privateChargeStation.getLatitude().doubleValue());
                        dto.setPrice(privateChargeStation.getPrice().doubleValue());
                        dto.setOpenStatus(privateChargeStation.getOpenStatus());
                        dto.setRating(privateChargeStation.getRating() != null
                                ? privateChargeStation.getRating().doubleValue()
                                : null);
                        dto.setDistance(GeoUtils.calculateDistance(longitude, latitude,
                                privateStation.getLongitude(), privateStation.getLatitude()));
                        dto.setStationType(0); // 0-私桩

                        // 筛选在半径范围内的私桩
                        if (dto.getDistance() <= radius) {
                            allStations.add(dto);
                        }
                    }
                }
            }
        }

        // 查询公桩
        if (stationType == 1 || stationType == 2) {
            List<PublicChargeStation> publicStationList = userPublicChargeStationMapper
                    .selectPublicStationsByGeoRange(minLng, maxLng, minLat, maxLat);

            if (publicStationList != null && !publicStationList.isEmpty()) {
                for (PublicChargeStation publicStation : publicStationList) {
                    StationSearchResultDTO dto = new StationSearchResultDTO();
                    dto.setId(publicStation.getId());
                    dto.setName(publicStation.getName());
                    dto.setLocationName(publicStation.getLocationName());
                    dto.setLongitude(publicStation.getLongitude());
                    dto.setLatitude(publicStation.getLatitude());
                    dto.setPrice(publicStation.getPrice());
                    dto.setOpenStatus(publicStation.getOpenStatus());
                    dto.setRating(publicStation.getRating());
                    dto.setDistance(GeoUtils.calculateDistance(longitude, latitude,
                            publicStation.getLongitude(), publicStation.getLatitude()));
                    dto.setStationType(1); // 1-公桩

                    // 筛选在半径范围内的公桩
                    if (dto.getDistance() <= radius) {
                        allStations.add(dto);
                    }
                }
            }
        }

        // 3. 根据排序规则排序
        sortStations(allStations, sortType);

        return allStations;
    }

    /**
     * 根据排序规则对充电桩列表进行排序
     * 
     * @param stations 充电桩列表
     * @param sortType 排序类型：1-评分优先，2-距离优先，3-价格优先
     */
    private void sortStations(List<StationSearchResultDTO> stations, int sortType) {
        if (stations == null || stations.isEmpty()) {
            return;
        }

        Comparator<StationSearchResultDTO> comparator;

        switch (sortType) {
            case 1: // 评分优先
                comparator = Comparator
                        .comparing(StationSearchResultDTO::getRating, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(StationSearchResultDTO::getDistance);
                break;
            case 2: // 距离优先
                comparator = Comparator.comparing(StationSearchResultDTO::getDistance);
                break;
            case 3: // 价格优先
                comparator = Comparator
                        .comparing(StationSearchResultDTO::getPrice, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StationSearchResultDTO::getDistance);
                break;
            default: // 默认距离优先
                comparator = Comparator.comparing(StationSearchResultDTO::getDistance);
        }

        stations.sort(comparator);
    }
}