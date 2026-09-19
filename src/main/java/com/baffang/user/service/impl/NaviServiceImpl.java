package com.baffang.user.service.impl;

import com.baffang.common.entity.PublicChargeStation;
import com.baffang.common.util.GeoUtils;
import com.baffang.common.util.GeoUtils.GeoPoint;
import com.baffang.user.dto.LongRoutePlanDTO;
import com.baffang.user.dto.LongRoutePlanDTO20;
import com.baffang.user.dto.ShortRoutePlanDTO;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.NaviService;
import com.baffang.user.vo.NaviPrivateStationVO;
import com.baffang.user.vo.LongRoutePlanVO;
import com.baffang.user.vo.LongRoutePlanVO20;
import com.baffang.user.vo.ShortRoutePlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 导航服务实现类
 */
@Service("naviServiceImpl")
public class NaviServiceImpl implements NaviService {

    @Autowired
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    @Autowired
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    /**
     * 默认搜索半径（米）
     */
    private static final int DEFAULT_RADIUS = 3000;

    /**
     * 最大搜索半径（米）
     */
    private static final int MAX_RADIUS = 10000;

    /**
     * 经纬度范围扩展值（用于数据库初步筛选）
     */
    private static final double LNG_LAT_RANGE = 0.3;

    /**
     * 默认目标电量（%）
     */
    private static final int DEFAULT_TARGET_BATTERY = 80;

    /**
     * 默认车辆满电续航（公里）
     */
    private static final int DEFAULT_VEHICLE_RANGE = 200;

    /**
     * 默认优先公桩
     */
    private static final int DEFAULT_PREFER_STATION_TYPE = 1;

    /**
     * 默认偏好类型：距离最近
     */
    private static final int DEFAULT_PREFER_TYPE = 1;

    /**
     * 底线电量（%）
     */
    private static final int BOTTOM_LINE_BATTERY = 10;

    /**
     * 默认路线类型：最快
     */
    private static final int DEFAULT_ROUTE_TYPE = 1;

    /**
     * 最小搜索宽度（米）
     */
    private static final int MIN_SEARCH_WIDTH = 20000;

    /**
     * 最大搜索宽度（米）
     */
    private static final int MAX_SEARCH_WIDTH = 50000;

    /**
     * 地球半径（米）
     */
    private static final double EARTH_RADIUS = 6371000;

    @Override
    public Map<String, Object> searchPrivateStations(String keyword, Double currentLng, Double currentLat,
            Integer radius) {
        // 1. 参数处理
        // 设置默认半径
        if (radius == null || radius <= 0) {
            radius = DEFAULT_RADIUS;
        } else if (radius > MAX_RADIUS) {
            // 限制最大半径
            radius = MAX_RADIUS;
        }

        // 2. 构建数据库查询参数
        Double minLng = null;
        Double maxLng = null;
        Double minLat = null;
        Double maxLat = null;

        if (currentLng != null && currentLat != null) {
            // 计算经纬度范围（±0.3）
            minLng = currentLng - LNG_LAT_RANGE;
            maxLng = currentLng + LNG_LAT_RANGE;
            minLat = currentLat - LNG_LAT_RANGE;
            maxLat = currentLat + LNG_LAT_RANGE;
        }

        // 3. 从数据库查询符合条件的私桩
        List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                .selectPrivateStationsByGeoRange(keyword, minLng, maxLng, minLat, maxLat);

        // 4. 使用GeoUtils进行半径筛选
        List<NaviPrivateStationVO> filteredStations = new ArrayList<>();
        if (currentLng != null && currentLat != null && privateStationList != null && !privateStationList.isEmpty()) {
            // 转换为GeoPoint列表
            List<GeoPoint> geoPoints = new ArrayList<>();
            Map<GeoPoint, NaviPrivateStationVO> pointStationMap = new HashMap<>();
            for (NaviPrivateStationVO station : privateStationList) {
                if (station.getLongitude() != null && station.getLatitude() != null) {
                    GeoPoint point = new GeoPoint(station.getLongitude(), station.getLatitude());
                    geoPoints.add(point);
                    pointStationMap.put(point, station);
                }
            }

            // 筛选半径范围内的点
            List<GeoPoint> filteredPoints = GeoUtils.searchPointsInRadius(currentLng, currentLat, radius, geoPoints);

            // 将筛选结果转换回NaviPrivateStationVO列表，并设置距离
            for (GeoPoint point : filteredPoints) {
                if (pointStationMap.containsKey(point)) {
                    NaviPrivateStationVO station = pointStationMap.get(point);
                    station.setDistance(point.getDistance()); // 设置距离
                    filteredStations.add(station);
                }
            }
        } else {
            // 如果没有经纬度参数，直接返回所有查询结果
            filteredStations = privateStationList != null ? privateStationList : new ArrayList<>();
        }

        // 5. 构建结果返回
        Map<String, Object> result = new HashMap<>();
        result.put("list", filteredStations != null ? filteredStations : new ArrayList<>());
        result.put("total", filteredStations != null ? filteredStations.size() : 0);

        return result;
    }

    @Override
    public ShortRoutePlanVO planShortRoute(ShortRoutePlanDTO dto) {
        // 1. 参数处理
        // 设置默认值
        Integer targetBattery = dto.getTargetBattery() != null ? dto.getTargetBattery() : DEFAULT_TARGET_BATTERY;
        Integer vehicleRange = dto.getVehicleRange() != null ? dto.getVehicleRange() : DEFAULT_VEHICLE_RANGE;
        Integer preferStationType = dto.getPreferStationType() != null ? dto.getPreferStationType()
                : DEFAULT_PREFER_STATION_TYPE;
        Integer preferType = dto.getPreferType() != null ? dto.getPreferType() : DEFAULT_PREFER_TYPE;

        // 2. 计算可达距离
        // 计算当前电量减去底线电量后的剩余可用电量百分比
        int availableBatteryPercent = dto.getVehicleBattery() - BOTTOM_LINE_BATTERY;
        if (availableBatteryPercent <= 0) {
            // 如果当前电量已经低于底线电量，返回空结果
            ShortRoutePlanVO result = new ShortRoutePlanVO();
            result.setRecommendStation(null);
            result.setAlternativeStations(new ArrayList<>());
            return result;
        }

        // 计算可达距离（公里）：(当前电量 - 底线电量) * 续航里程 / 100
        double availableRange = availableBatteryPercent * vehicleRange / 100.0;
        // 转换为米
        int availableRangeMeters = (int) (availableRange * 1000);

        // 3. 构建数据库查询参数
        Double currentLng = dto.getCurrentLng();
        Double currentLat = dto.getCurrentLat();

        // 计算经纬度范围
        // 使用更大的范围进行初步筛选，确保不会漏掉附近的充电桩
        Double minLng = currentLng - LNG_LAT_RANGE;
        Double maxLng = currentLng + LNG_LAT_RANGE;
        Double minLat = currentLat - LNG_LAT_RANGE;
        Double maxLat = currentLat + LNG_LAT_RANGE;

        // 4. 查询符合条件的充电桩
        List<ShortRoutePlanVO.StationVO> allStations = new ArrayList<>();

        // 查询私桩
        if (preferStationType == 0) {
            List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                    .selectPrivateStationsByGeoRange(null, minLng, maxLng, minLat, maxLat);
            if (privateStationList != null && !privateStationList.isEmpty()) {
                for (NaviPrivateStationVO privateStation : privateStationList) {
                    ShortRoutePlanVO.StationVO stationVO = convertToStationVO(privateStation, currentLng, currentLat,
                            0);
                    allStations.add(stationVO);
                }
            }
        }

        // 查询公桩
        if (preferStationType == 1 || preferStationType == null) {
            List<PublicChargeStation> publicStationList = userPublicChargeStationMapper
                    .selectPublicStationsByGeoRange(minLng, maxLng, minLat, maxLat);
            if (publicStationList != null && !publicStationList.isEmpty()) {
                for (PublicChargeStation publicStation : publicStationList) {
                    ShortRoutePlanVO.StationVO stationVO = convertToStationVO(publicStation, currentLng, currentLat, 1);
                    allStations.add(stationVO);
                }
            }
        }

        // 5. 使用GeoUtils进行距离筛选
        List<ShortRoutePlanVO.StationVO> distanceFilteredStations = new ArrayList<>();
        if (currentLng != null && currentLat != null && !allStations.isEmpty()) {
            for (ShortRoutePlanVO.StationVO station : allStations) {
                if (station.getDistance() != null && station.getDistance() <= availableRangeMeters) {
                    distanceFilteredStations.add(station);
                }
            }
        } else {
            distanceFilteredStations = allStations;
        }

        // 6. 根据充电桩参数筛选
        List<ShortRoutePlanVO.StationVO> paramFilteredStations = new ArrayList<>();
        if (dto.getStationParams() != null) {
            Integer currentType = dto.getStationParams().getCurrentType();
            Integer maxChargePower = dto.getStationParams().getMaxChargePower();

            for (ShortRoutePlanVO.StationVO station : distanceFilteredStations) {
                boolean match = true;

                if (currentType != null && !currentType.equals(station.getCurrentType())) {
                    match = false;
                }

                if (maxChargePower != null && station.getMaxChargePower() != null &&
                        station.getMaxChargePower() < maxChargePower) {
                    match = false;
                }

                if (match) {
                    paramFilteredStations.add(station);
                }
            }
        } else {
            paramFilteredStations = distanceFilteredStations;
        }

        // 8. 根据偏好排序推荐充电桩
        sortStationsByPreferType(paramFilteredStations, preferType);

        // 9. 构建返回结果
        ShortRoutePlanVO result = new ShortRoutePlanVO();

        if (!paramFilteredStations.isEmpty()) {
            // 设置推荐充电桩
            result.setRecommendStation(paramFilteredStations.get(0));

            // 设置备选充电桩（最多返回4个，从排序后的结果中获取推荐之后的）
            List<ShortRoutePlanVO.StationVO> alternativeStations = new ArrayList<>();
            for (int i = 1; i < paramFilteredStations.size() && i <= 4; i++) {
                alternativeStations.add(paramFilteredStations.get(i));
            }
            result.setAlternativeStations(alternativeStations);
        } else {
            // 如果没有找到充电桩，返回空列表
            result.setRecommendStation(null);
            result.setAlternativeStations(new ArrayList<>());
        }

        return result;
    }

    /**
     * 将NaviPrivateStationVO转换为ShortRoutePlanVO.StationVO
     * 
     * @param privateStation 私桩信息
     * @param currentLng     当前经度
     * @param currentLat     当前纬度
     * @param stationType    充电桩类型：0-私桩，1-公桩
     * @return 转换后的StationVO
     */
    private ShortRoutePlanVO.StationVO convertToStationVO(NaviPrivateStationVO privateStation,
            Double currentLng, Double currentLat,
            Integer stationType) {
        ShortRoutePlanVO.StationVO stationVO = new ShortRoutePlanVO.StationVO();

        stationVO.setStationId(privateStation.getId());
        stationVO.setStationName(privateStation.getName());
        stationVO.setLongitude(privateStation.getLongitude());
        stationVO.setLatitude(privateStation.getLatitude());
        stationVO.setAddress(privateStation.getLocationName());

        // 计算距离
        if (currentLng != null && currentLat != null && privateStation.getLongitude() != null
                && privateStation.getLatitude() != null) {
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    privateStation.getLongitude(), privateStation.getLatitude());
            stationVO.setDistance((int) Math.round(distance));
        }

        stationVO.setStationType(stationType);
        // NaviPrivateStationVO中没有这些字段，设置为null
        stationVO.setCurrentType(null);
        stationVO.setMaxChargePower(null);
        stationVO.setPrice(null);
        stationVO.setRating(null);
        stationVO.setAvailableCount(privateStation.getMaxVehicles() - privateStation.getCurrentVehicles());

        // 由于缺少功率信息，无法计算预计充电时间，设置为null
        stationVO.setEstimatedChargeTime(null);

        // 计算预计到达时间（简化计算）
        // 假设平均行驶速度为30km/h
        if (stationVO.getDistance() != null) {
            double distanceKm = stationVO.getDistance() / 1000.0;
            double hours = distanceKm / 30.0;
            stationVO.setEstimatedArrivalTime((int) Math.round(hours * 60));
        }

        return stationVO;
    }

    /**
     * 将PublicChargeStation转换为ShortRoutePlanVO.StationVO
     * 
     * @param publicStation 公桩信息
     * @param currentLng    当前经度
     * @param currentLat    当前纬度
     * @param stationType   充电桩类型：0-私桩，1-公桩
     * @return 转换后的StationVO
     */
    private ShortRoutePlanVO.StationVO convertToStationVO(PublicChargeStation publicStation,
            Double currentLng, Double currentLat,
            Integer stationType) {
        ShortRoutePlanVO.StationVO stationVO = new ShortRoutePlanVO.StationVO();

        stationVO.setStationId(publicStation.getId());
        stationVO.setStationName(publicStation.getName());
        stationVO.setLongitude(publicStation.getLongitude());
        stationVO.setLatitude(publicStation.getLatitude());
        stationVO.setAddress(publicStation.getLocationName());

        // 计算距离
        if (currentLng != null && currentLat != null && publicStation.getLongitude() != null
                && publicStation.getLatitude() != null) {
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    publicStation.getLongitude(), publicStation.getLatitude());
            stationVO.setDistance((int) Math.round(distance));
        }

        stationVO.setStationType(stationType);
        stationVO.setCurrentType(publicStation.getCurrentType());
        stationVO.setMaxChargePower(publicStation.getMaxChargePower());
        stationVO.setPrice(publicStation.getPrice());
        stationVO.setRating(publicStation.getRating());
        stationVO.setAvailableCount(publicStation.getMaxVehicles() - publicStation.getCurrentVehicles());

        // 计算预计充电时间（简化计算）
        // 假设平均充电功率为最大功率的70%
        double averagePower = publicStation.getMaxChargePower() * 0.7 / 1000.0; // 转换为kW
        if (averagePower > 0) {
            // 假设每度电可行驶约6公里
            double requiredKwh = (publicStation.getMaxVehicles() - publicStation.getCurrentVehicles()) * 6 / 100.0;
            double hours = requiredKwh / averagePower;
            stationVO.setEstimatedChargeTime((int) Math.round(hours * 60));
        }

        // 计算预计到达时间（简化计算）
        // 假设平均行驶速度为30km/h
        if (stationVO.getDistance() != null) {
            double distanceKm = stationVO.getDistance() / 1000.0;
            double hours = distanceKm / 30.0;
            stationVO.setEstimatedArrivalTime((int) Math.round(hours * 60));
        }

        return stationVO;
    }

    /**
     * 根据偏好类型对充电桩进行排序
     * 
     * @param stations   充电桩列表
     * @param preferType 偏好类型：1-距离最近，2-价格最低，3-评分最高，4-空位最多
     */
    private void sortStationsByPreferType(List<ShortRoutePlanVO.StationVO> stations, Integer preferType) {
        if (stations == null || stations.isEmpty()) {
            return;
        }

        Comparator<ShortRoutePlanVO.StationVO> comparator = null;

        switch (preferType) {
            case 1: // 距离最近
                comparator = Comparator.comparingInt(ShortRoutePlanVO.StationVO::getDistance);
                break;
            case 2: // 价格最低
                comparator = Comparator.comparingDouble(ShortRoutePlanVO.StationVO::getPrice);
                break;
            case 3: // 评分最高
                comparator = Comparator.comparingDouble(ShortRoutePlanVO.StationVO::getRating).reversed();
                break;
            case 4: // 空位最多
                comparator = Comparator.comparingInt(ShortRoutePlanVO.StationVO::getAvailableCount).reversed();
                break;
            default:
                // 默认距离最近
                comparator = Comparator.comparingInt(ShortRoutePlanVO.StationVO::getDistance);
        }

        stations.sort(comparator);
    }

    @Override
    public LongRoutePlanVO20 planLongRoute20(LongRoutePlanDTO20 dto) {
        // 暂时返回空实现
        return null;
    }

    @Override
    public LongRoutePlanVO planLongRoute(LongRoutePlanDTO dto) {
        // 1. 参数处理
        // 设置默认值
        Integer vehicleRange = dto.getVehicleRange() != null ? dto.getVehicleRange() : DEFAULT_VEHICLE_RANGE;
        Integer currentBattery = dto.getCurrentBattery() != null ? dto.getCurrentBattery() : DEFAULT_TARGET_BATTERY;
        Integer bottomLineBattery = dto.getBottomLineBattery() != null ? dto.getBottomLineBattery()
                : BOTTOM_LINE_BATTERY;
        Integer preferStationType = dto.getPreferStationType() != null ? dto.getPreferStationType()
                : DEFAULT_PREFER_STATION_TYPE;
        Integer preferType = dto.getPreferType() != null ? dto.getPreferType() : DEFAULT_PREFER_TYPE;

        // 2. 路线范围确定
        // 计算起点到终点的直线距离
        double straightDistance = GeoUtils.calculateDistance(dto.getStartLng(), dto.getStartLat(),
                dto.getEndLng(), dto.getEndLat());

        // 根据路线长度动态确定搜索宽度
        double searchWidth = calculateSearchWidth(straightDistance);

        // 计算经纬度范围
        double[] geoRange = calculateGeoRange(dto.getStartLng(), dto.getStartLat(),
                dto.getEndLng(), dto.getEndLat(), searchWidth);
        double minLng = geoRange[0];
        double maxLng = geoRange[1];
        double minLat = geoRange[2];
        double maxLat = geoRange[3];

        // 3. 充电桩筛选
        List<LongRoutePlanVO.StationVO> filteredStations = filterStations(minLng, maxLng, minLat, maxLat,
                dto.getStartLng(), dto.getStartLat(),
                dto.getEndLng(), dto.getEndLat(),
                searchWidth, dto);

        // 4. 路线分段计算
        // 计算最大安全行驶距离（公里）
        double maxSafeDistance = vehicleRange * (100 - bottomLineBattery) / 100.0;

        // 计算实际路线距离（这里简化为直线距离，实际应用中应调用地图服务）
        double actualRouteDistance = straightDistance / 1000.0; // 转换为公里

        // 计算需要的充电次数
        int requiredChargingCount = (int) Math.ceil(actualRouteDistance / maxSafeDistance) - 1;

        // 生成分段点
        List<GeoPoint> segmentPoints = new ArrayList<>();
        for (int i = 1; i <= requiredChargingCount; i++) {
            double ratio = i * maxSafeDistance / actualRouteDistance;
            GeoPoint point = calculateSegmentPoint(dto.getStartLng(), dto.getStartLat(),
                    dto.getEndLng(), dto.getEndLat(), ratio);
            segmentPoints.add(point);
        }

        // 5. 充电点选择
        LongRoutePlanVO result = new LongRoutePlanVO();

        // 生成充电停靠点列表
        List<LongRoutePlanVO.ChargingStop> chargingStops = new ArrayList<>();
        Set<Integer> selectedStationIds = new HashSet<>();

        // 当前位置和电量
        double currentLng = dto.getStartLng();
        double currentLat = dto.getStartLat();
        double remainingBattery = currentBattery;

        // 总距离和总时间
        double totalDistance = 0;
        double totalTime = 0;

        // 为每个分段点选择最优充电桩
        boolean isSuccess = true;
        for (int i = 0; i < segmentPoints.size(); i++) {
            GeoPoint segmentPoint = segmentPoints.get(i);

            // 为当前分段点选择最优充电桩（考虑方向和不重复选择）
            LongRoutePlanVO.StationVO bestStation = selectBestStationAlongRoute(filteredStations, currentLng,
                    currentLat,
                    segmentPoint.getLongitude(), segmentPoint.getLatitude(),
                    dto.getEndLng(), dto.getEndLat(),
                    remainingBattery, bottomLineBattery,
                    vehicleRange, preferType, selectedStationIds);

            if (bestStation != null) {
                // 计算到充电桩的距离
                double distanceToStation = GeoUtils.calculateDistance(currentLng, currentLat,
                        bestStation.getLongitude(), bestStation.getLatitude());

                // 计算到达时的电量
                double batteryConsumed = (distanceToStation / 1000.0 / vehicleRange) * 100;
                double arrivalBattery = remainingBattery - batteryConsumed;

                // 计算需要充电的电量（充到80%）
                double chargingAmount = 80 - arrivalBattery;

                // 计算充电时间
                int estimatedChargingTime = calculateChargingTime(bestStation, chargingAmount, vehicleRange);

                // 计算距终点剩余距离
                double distanceToEnd = GeoUtils.calculateDistance(bestStation.getLongitude(), bestStation.getLatitude(),
                        dto.getEndLng(), dto.getEndLat());

                // 查找附近3个备选充电桩
                List<LongRoutePlanVO.StationVO> nearbyStations = findNearbyStations(filteredStations,
                        bestStation.getLongitude(),
                        bestStation.getLatitude(), 10000, 3);

                // 创建充电点信息
                LongRoutePlanVO.ChargingStop chargingStop = new LongRoutePlanVO.ChargingStop();
                chargingStop.setStopNumber(i + 1); // 停靠点标号从1开始
                chargingStop.setStation(bestStation);
                chargingStop.setArrivalBattery(arrivalBattery);
                chargingStop.setChargingAmount(chargingAmount);
                chargingStop.setEstimatedChargingTime(estimatedChargingTime);
                chargingStop.setDistanceFromPrev(distanceToStation / 1000.0);
                chargingStop.setDistanceToEnd(distanceToEnd / 1000.0);
                chargingStop.setAlternativeStations(nearbyStations);

                chargingStops.add(chargingStop);

                // 记录已选择的充电桩
                selectedStationIds.add(bestStation.getStationId());

                // 更新当前位置和电量
                currentLng = bestStation.getLongitude();
                currentLat = bestStation.getLatitude();
                remainingBattery = 80; // 充电到80%

                // 更新总距离和时间
                totalDistance += distanceToStation / 1000.0;
                totalTime += (distanceToStation / 1000.0 / 100.0) * 60; // 假设平均速度100km/h
                totalTime += estimatedChargingTime; // 加上充电时间
            } else {
                isSuccess = false;
                break;
            }
        }

        // 检查是否能到达终点，如果不能，增加额外的充电点
        if (isSuccess) {
            // 计算最后一段到终点的距离
            double distanceToEnd = GeoUtils.calculateDistance(currentLng, currentLat,
                    dto.getEndLng(), dto.getEndLat()) / 1000.0; // 转换为公里

            // 检查是否在最大安全行驶距离范围内
            while (distanceToEnd > maxSafeDistance && isSuccess) {
                // 计算需要添加的充电点位置（在当前位置和终点之间）
                GeoPoint extraSegmentPoint = calculateSegmentPoint(currentLng, currentLat,
                        dto.getEndLng(), dto.getEndLat(), 0.5); // 取中间点

                // 为额外的分段点选择最优充电桩
                LongRoutePlanVO.StationVO bestStation = selectBestStationAlongRoute(filteredStations, currentLng,
                        currentLat,
                        extraSegmentPoint.getLongitude(), extraSegmentPoint.getLatitude(),
                        dto.getEndLng(), dto.getEndLat(),
                        remainingBattery, bottomLineBattery,
                        vehicleRange, preferType, selectedStationIds);

                if (bestStation != null) {
                    // 计算到充电桩的距离
                    double distanceToStation = GeoUtils.calculateDistance(currentLng, currentLat,
                            bestStation.getLongitude(), bestStation.getLatitude()) / 1000.0; // 转换为公里

                    // 计算到达时的电量
                    double batteryConsumed = (distanceToStation / vehicleRange) * 100;
                    double arrivalBattery = remainingBattery - batteryConsumed;

                    // 计算需要充电的电量（充到80%）
                    double chargingAmount = 80 - arrivalBattery;

                    // 计算充电时间
                    int estimatedChargingTime = calculateChargingTime(bestStation, chargingAmount, vehicleRange);

                    // 计算距终点剩余距离
                    double newDistanceToEnd = GeoUtils.calculateDistance(bestStation.getLongitude(),
                            bestStation.getLatitude(),
                            dto.getEndLng(), dto.getEndLat()) / 1000.0; // 转换为公里

                    // 查找附近3个备选充电桩
                    List<LongRoutePlanVO.StationVO> nearbyStations = findNearbyStations(filteredStations,
                            bestStation.getLongitude(),
                            bestStation.getLatitude(), 10000, 3);

                    // 创建充电点信息
                    LongRoutePlanVO.ChargingStop chargingStop = new LongRoutePlanVO.ChargingStop();
                    chargingStop.setStopNumber(chargingStops.size() + 1); // 停靠点标号递增
                    chargingStop.setStation(bestStation);
                    chargingStop.setArrivalBattery(arrivalBattery);
                    chargingStop.setChargingAmount(chargingAmount);
                    chargingStop.setEstimatedChargingTime(estimatedChargingTime);
                    chargingStop.setDistanceFromPrev(distanceToStation);
                    chargingStop.setDistanceToEnd(newDistanceToEnd);
                    chargingStop.setAlternativeStations(nearbyStations);

                    chargingStops.add(chargingStop);

                    // 记录已选择的充电桩
                    selectedStationIds.add(bestStation.getStationId());

                    // 更新当前位置和电量
                    currentLng = bestStation.getLongitude();
                    currentLat = bestStation.getLatitude();
                    remainingBattery = 80; // 充电到80%

                    // 更新总距离和时间
                    totalDistance += distanceToStation;
                    totalTime += (distanceToStation / 100.0) * 60; // 假设平均速度100km/h
                    totalTime += estimatedChargingTime; // 加上充电时间

                    // 更新到终点的距离
                    distanceToEnd = newDistanceToEnd;
                } else {
                    isSuccess = false;
                }
            }

            // 计算最后一段到终点的距离和时间
            double finalDistanceToEnd = GeoUtils.calculateDistance(currentLng, currentLat,
                    dto.getEndLng(), dto.getEndLat()) / 1000.0; // 转换为公里
            totalDistance += finalDistanceToEnd;
            totalTime += (finalDistanceToEnd / 100.0) * 60;
        }

        if (isSuccess && !chargingStops.isEmpty()) {
            // 设置成功结果
            result.setMessage("规划成功");
            result.setTotalDistance(totalDistance);
            result.setEstimatedTime(totalTime / 60.0); // 转换为小时
            result.setChargingStops(chargingStops);
        } else {
            // 设置失败结果
            result.setMessage("充电桩不足，请谨慎出行");
            result.setTotalDistance(null);
            result.setEstimatedTime(null);
            result.setChargingStops(new ArrayList<>());
        }

        return result;
    }

    /**
     * 根据路线长度计算搜索宽度
     * 
     * @param straightDistance 直线距离（米）
     * @return 搜索宽度（米）
     */
    private double calculateSearchWidth(double straightDistance) {
        if (straightDistance <= 100000) { // 100公里
            return MIN_SEARCH_WIDTH;
        } else if (straightDistance <= 500000) { // 500公里
            return 30000;
        } else if (straightDistance <= 1000000) { // 1000公里
            return 40000;
        } else {
            return MAX_SEARCH_WIDTH;
        }
    }

    /**
     * 计算路线的地理范围
     * 
     * @param startLng    起点经度
     * @param startLat    起点纬度
     * @param endLng      终点经度
     * @param endLat      终点纬度
     * @param searchWidth 搜索宽度（米）
     * @return 经纬度范围 [minLng, maxLng, minLat, maxLat]
     */
    private double[] calculateGeoRange(double startLng, double startLat,
            double endLng, double endLat, double searchWidth) {
        // 计算路线两端点的经纬度范围
        double lng1 = startLng - searchWidth / (EARTH_RADIUS * Math.cos(Math.toRadians(startLat)) * Math.PI / 180);
        double lng2 = startLng + searchWidth / (EARTH_RADIUS * Math.cos(Math.toRadians(startLat)) * Math.PI / 180);
        double lat1 = startLat - searchWidth / (EARTH_RADIUS * Math.PI / 180);
        double lat2 = startLat + searchWidth / (EARTH_RADIUS * Math.PI / 180);

        double lng3 = endLng - searchWidth / (EARTH_RADIUS * Math.cos(Math.toRadians(endLat)) * Math.PI / 180);
        double lng4 = endLng + searchWidth / (EARTH_RADIUS * Math.cos(Math.toRadians(endLat)) * Math.PI / 180);
        double lat3 = endLat - searchWidth / (EARTH_RADIUS * Math.PI / 180);
        double lat4 = endLat + searchWidth / (EARTH_RADIUS * Math.PI / 180);

        // 取最小和最大经纬度
        double minLng = Math.min(Math.min(lng1, lng2), Math.min(lng3, lng4));
        double maxLng = Math.max(Math.max(lng1, lng2), Math.max(lng3, lng4));
        double minLat = Math.min(Math.min(lat1, lat2), Math.min(lat3, lat4));
        double maxLat = Math.max(Math.max(lat1, lat2), Math.max(lat3, lat4));

        return new double[] { minLng, maxLng, minLat, maxLat };
    }

    /**
     * 筛选符合条件的充电桩
     * 
     * @param minLng      最小经度
     * @param maxLng      最大经度
     * @param minLat      最小纬度
     * @param maxLat      最大纬度
     * @param startLng    起点经度
     * @param startLat    起点纬度
     * @param endLng      终点经度
     * @param endLat      终点纬度
     * @param searchWidth 搜索宽度
     * @param dto         请求参数
     * @return 筛选后的充电桩列表
     */
    private List<LongRoutePlanVO.StationVO> filterStations(double minLng, double maxLng, double minLat, double maxLat,
            double startLng, double startLat,
            double endLng, double endLat,
            double searchWidth, LongRoutePlanDTO dto) {
        List<LongRoutePlanVO.StationVO> filteredStations = new ArrayList<>();

        // 查询公桩（preferStationType=0或null或2时查询）
        if (dto.getPreferStationType() == 0 || dto.getPreferStationType() == null || dto.getPreferStationType() == 2) {
            List<PublicChargeStation> publicStationList = userPublicChargeStationMapper
                    .selectPublicStationsByGeoRange(minLng, maxLng, minLat, maxLat);
            if (publicStationList != null && !publicStationList.isEmpty()) {
                for (PublicChargeStation publicStation : publicStationList) {
                    // 检查是否在路线范围内
                    double distanceToLine = calculateDistanceToLine(publicStation.getLongitude(),
                            publicStation.getLatitude(),
                            startLng, startLat, endLng, endLat);
                    if (distanceToLine <= searchWidth) {
                        LongRoutePlanVO.StationVO stationVO = convertToLongRouteStationVO(publicStation, startLng,
                                startLat, 1);
                        filteredStations.add(stationVO);
                    }
                }
            }
        }

        // 查询私桩（preferStationType=1或2时查询）
        if (dto.getPreferStationType() == 1 || dto.getPreferStationType() == 2) {
            List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                    .selectPrivateStationsByGeoRange(null, minLng, maxLng, minLat, maxLat);
            if (privateStationList != null && !privateStationList.isEmpty()) {
                for (NaviPrivateStationVO privateStation : privateStationList) {
                    // 检查是否在路线范围内
                    double distanceToLine = calculateDistanceToLine(privateStation.getLongitude(),
                            privateStation.getLatitude(),
                            startLng, startLat, endLng, endLat);
                    if (distanceToLine <= searchWidth) {
                        LongRoutePlanVO.StationVO stationVO = convertToLongRouteStationVO(privateStation, startLng,
                                startLat, 0);
                        filteredStations.add(stationVO);
                    }
                }
            }
        }

        // 根据用户偏好筛选
        List<LongRoutePlanVO.StationVO> paramFilteredStations = new ArrayList<>();
        for (LongRoutePlanVO.StationVO station : filteredStations) {
            boolean match = true;

            if (dto.getPreferredCurrentType() != null && station.getCurrentType() != null &&
                    station.getCurrentType() != dto.getPreferredCurrentType()) {
                match = false;
            }

            if (dto.getMinChargePower() != null && station.getMaxChargePower() != null &&
                    station.getMaxChargePower() < dto.getMinChargePower()) {
                match = false;
            }

            if (match) {
                paramFilteredStations.add(station);
            }
        }

        return paramFilteredStations;
    }

    /**
     * 计算点到直线的距离
     * 
     * @param pointLng     点经度
     * @param pointLat     点纬度
     * @param lineStartLng 直线起点经度
     * @param lineStartLat 直线起点纬度
     * @param lineEndLng   直线终点经度
     * @param lineEndLat   直线终点纬度
     * @return 距离（米）
     */
    private double calculateDistanceToLine(double pointLng, double pointLat,
            double lineStartLng, double lineStartLat,
            double lineEndLng, double lineEndLat) {
        // 计算向量
        double vx1 = Math.toRadians(pointLng - lineStartLng) * EARTH_RADIUS * Math.cos(Math.toRadians(lineStartLat));
        double vy1 = Math.toRadians(pointLat - lineStartLat) * EARTH_RADIUS;
        double vx2 = Math.toRadians(lineEndLng - lineStartLng) * EARTH_RADIUS * Math.cos(Math.toRadians(lineStartLat));
        double vy2 = Math.toRadians(lineEndLat - lineStartLat) * EARTH_RADIUS;

        // 计算向量叉积
        double crossProduct = vx1 * vy2 - vy1 * vx2;

        // 计算直线长度
        double lineLength = Math.sqrt(vx2 * vx2 + vy2 * vy2);

        // 计算点到直线的距离
        return Math.abs(crossProduct) / lineLength;
    }

    /**
     * 计算路线上的分段点
     * 
     * @param startLng 起点经度
     * @param startLat 起点纬度
     * @param endLng   终点经度
     * @param endLat   终点纬度
     * @param ratio    距离比例（0-1）
     * @return 分段点坐标
     */
    private GeoPoint calculateSegmentPoint(double startLng, double startLat,
            double endLng, double endLat, double ratio) {
        // 线性插值计算分段点
        double segmentLng = startLng + (endLng - startLng) * ratio;
        double segmentLat = startLat + (endLat - startLat) * ratio;

        return new GeoPoint(segmentLng, segmentLat);
    }

    /**
     * 选择最优充电桩
     * 
     * @param stations          充电桩列表
     * @param currentLng        当前经度
     * @param currentLat        当前纬度
     * @param remainingBattery  当前剩余电量
     * @param bottomLineBattery 电量底线
     * @param vehicleRange      车辆续航
     * @param preferType        偏好类型
     * @return 最优充电桩
     */
    private LongRoutePlanVO.StationVO selectBestStation(List<LongRoutePlanVO.StationVO> stations,
            double currentLng, double currentLat,
            double remainingBattery, int bottomLineBattery,
            int vehicleRange, int preferType) {
        if (stations == null || stations.isEmpty()) {
            return null;
        }

        // 计算当前可行驶距离
        double maxAvailableDistance = vehicleRange * (remainingBattery - bottomLineBattery) / 100.0 * 1000.0; // 转换为米

        // 筛选可到达的充电桩
        List<LongRoutePlanVO.StationVO> reachableStations = new ArrayList<>();
        for (LongRoutePlanVO.StationVO station : stations) {
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    station.getLongitude(), station.getLatitude());
            if (distance <= maxAvailableDistance) {
                reachableStations.add(station);
            }
        }

        if (reachableStations.isEmpty()) {
            return null;
        }

        // 根据偏好排序
        sortLongRouteStationsByPreferType(reachableStations, preferType, currentLng, currentLat);

        // 返回最优充电桩
        return reachableStations.get(0);
    }

    /**
     * 沿路线方向选择最优充电桩（避免重复选择）
     * 
     * @param stations           充电桩列表
     * @param currentLng         当前经度
     * @param currentLat         当前纬度
     * @param segmentLng         分段点经度
     * @param segmentLat         分段点纬度
     * @param endLng             终点经度
     * @param endLat             终点纬度
     * @param remainingBattery   当前剩余电量
     * @param bottomLineBattery  电量底线
     * @param vehicleRange       车辆续航
     * @param preferType         偏好类型
     * @param selectedStationIds 已选择的充电桩ID集合
     * @return 最优充电桩
     */
    private LongRoutePlanVO.StationVO selectBestStationAlongRoute(List<LongRoutePlanVO.StationVO> stations,
            double currentLng, double currentLat,
            double segmentLng, double segmentLat,
            double endLng, double endLat,
            double remainingBattery, int bottomLineBattery,
            int vehicleRange, int preferType,
            Set<Integer> selectedStationIds) {
        if (stations == null || stations.isEmpty()) {
            return null;
        }

        // 计算当前可行驶距离
        double maxAvailableDistance = vehicleRange * (remainingBattery - bottomLineBattery) / 100.0 * 1000.0; // 转换为米

        // 筛选可到达的充电桩
        List<LongRoutePlanVO.StationVO> reachableStations = new ArrayList<>();
        for (LongRoutePlanVO.StationVO station : stations) {
            // 跳过已选择的充电桩
            if (selectedStationIds.contains(station.getStationId())) {
                continue;
            }

            // 计算距离
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    station.getLongitude(), station.getLatitude());

            // 检查是否可到达
            if (distance <= maxAvailableDistance) {
                // 检查是否在正确的方向上
                if (isStationInCorrectDirection(currentLng, currentLat, station.getLongitude(), station.getLatitude(),
                        segmentLng, segmentLat, endLng, endLat)) {
                    reachableStations.add(station);
                }
            }
        }

        if (reachableStations.isEmpty()) {
            return null;
        }

        // 根据偏好排序
        sortLongRouteStationsByPreferType(reachableStations, preferType, currentLng, currentLat);

        // 返回最优充电桩
        return reachableStations.get(0);
    }

    /**
     * 检查充电桩是否在正确的行驶方向上
     * 
     * @param currentLng 当前经度
     * @param currentLat 当前纬度
     * @param stationLng 充电桩经度
     * @param stationLat 充电桩纬度
     * @param segmentLng 分段点经度
     * @param segmentLat 分段点纬度
     * @param endLng     终点经度
     * @param endLat     终点纬度
     * @return 是否在正确方向上
     */
    private boolean isStationInCorrectDirection(double currentLng, double currentLat,
            double stationLng, double stationLat,
            double segmentLng, double segmentLat,
            double endLng, double endLat) {
        // 计算当前位置到分段点的向量
        double vx1 = Math.toRadians(segmentLng - currentLng) * EARTH_RADIUS * Math.cos(Math.toRadians(currentLat));
        double vy1 = Math.toRadians(segmentLat - currentLat) * EARTH_RADIUS;

        // 计算当前位置到充电桩的向量
        double vx2 = Math.toRadians(stationLng - currentLng) * EARTH_RADIUS * Math.cos(Math.toRadians(currentLat));
        double vy2 = Math.toRadians(stationLat - currentLat) * EARTH_RADIUS;

        // 计算点积
        double dotProduct = vx1 * vx2 + vy1 * vy2;

        // 计算向量长度
        double length1 = Math.sqrt(vx1 * vx1 + vy1 * vy1);
        double length2 = Math.sqrt(vx2 * vx2 + vy2 * vy2);

        // 计算夹角余弦
        double cosine = dotProduct / (length1 * length2);

        // 如果夹角余弦大于0，说明在同一方向上
        return cosine > 0.5; // 允许一定的偏差角度
    }

    /**
     * 根据偏好类型对长途路线的充电桩进行排序
     * 
     * @param stations   充电桩列表
     * @param preferType 偏好类型：1-距离最近，2-充电最快，3-价格最低，4-评分最高
     * @param currentLng 当前经度
     * @param currentLat 当前纬度
     */
    private void sortLongRouteStationsByPreferType(List<LongRoutePlanVO.StationVO> stations,
            int preferType, double currentLng, double currentLat) {
        if (stations == null || stations.isEmpty()) {
            return;
        }

        Comparator<LongRoutePlanVO.StationVO> comparator = null;

        switch (preferType) {
            case 1: // 距离最近
                comparator = Comparator.comparingDouble(
                        (LongRoutePlanVO.StationVO station) -> GeoUtils.calculateDistance(currentLng, currentLat,
                                station.getLongitude(), station.getLatitude()));
                break;
            case 2: // 充电最快
                comparator = Comparator
                        .comparingInt((LongRoutePlanVO.StationVO station) -> station.getMaxChargePower() != null
                                ? station.getMaxChargePower()
                                : 0)
                        .reversed();
                break;
            case 3: // 价格最低
                comparator = Comparator.comparingDouble(
                        (LongRoutePlanVO.StationVO station) -> station.getPrice() != null ? station.getPrice()
                                : Double.MAX_VALUE);
                break;
            case 4: // 评分最高
                comparator = Comparator.comparingDouble(
                        (LongRoutePlanVO.StationVO station) -> station.getRating() != null ? station.getRating() : 0)
                        .reversed();
                break;
            default:
                // 默认距离最近
                comparator = Comparator.comparingDouble(
                        (LongRoutePlanVO.StationVO station) -> GeoUtils.calculateDistance(currentLng, currentLat,
                                station.getLongitude(), station.getLatitude()));
        }

        stations.sort(comparator);
    }

    /**
     * 计算充电时间
     * 
     * @param station        充电桩
     * @param chargingAmount 需要充电的电量百分比
     * @param vehicleRange   车辆续航
     * @return 预计充电时间（分钟）
     */
    private int calculateChargingTime(LongRoutePlanVO.StationVO station, double chargingAmount, int vehicleRange) {
        if (station.getMaxChargePower() == null || station.getMaxChargePower() <= 0) {
            return 60; // 默认60分钟
        }

        // 计算需要充电的度数
        double batteryCapacity = vehicleRange / 6.0; // 假设每度电行驶6公里
        double requiredKwh = batteryCapacity * chargingAmount / 100.0;

        // 计算平均充电功率（最大功率的70%）
        double averagePower = station.getMaxChargePower() * 0.7 / 1000.0; // 转换为kW

        // 计算充电时间（小时）
        double chargingTimeHours = requiredKwh / averagePower;

        // 转换为分钟并四舍五入
        return (int) Math.round(chargingTimeHours * 60);
    }

    /**
     * 查找附近的充电桩
     * 
     * @param stations 充电桩列表
     * @param lng      经度
     * @param lat      纬度
     * @param radius   半径（米）
     * @param limit    数量限制
     * @return 附近的充电桩列表
     */
    private List<LongRoutePlanVO.StationVO> findNearbyStations(List<LongRoutePlanVO.StationVO> stations,
            double lng, double lat,
            double radius, int limit) {
        List<LongRoutePlanVO.StationVO> nearbyStations = new ArrayList<>();

        // 创建临时列表并排序
        List<LongRoutePlanVO.StationVO> sortedStations = new ArrayList<>(stations);
        sortLongRouteStationsByPreferType(sortedStations, 1, lng, lat); // 按距离排序

        // 选择前limit个
        for (int i = 0; i < sortedStations.size() && i < limit; i++) {
            nearbyStations.add(sortedStations.get(i));
        }

        return nearbyStations;
    }

    /**
     * 将NaviPrivateStationVO转换为LongRoutePlanVO.StationVO
     */
    private LongRoutePlanVO.StationVO convertToLongRouteStationVO(NaviPrivateStationVO privateStation,
            Double currentLng, Double currentLat,
            Integer stationType) {
        LongRoutePlanVO.StationVO stationVO = new LongRoutePlanVO.StationVO();

        stationVO.setStationId(privateStation.getId());
        stationVO.setStationName(privateStation.getName());
        stationVO.setLongitude(privateStation.getLongitude());
        stationVO.setLatitude(privateStation.getLatitude());
        stationVO.setAddress(privateStation.getLocationName());

        // 计算距离
        if (currentLng != null && currentLat != null && privateStation.getLongitude() != null
                && privateStation.getLatitude() != null) {
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    privateStation.getLongitude(), privateStation.getLatitude());
            stationVO.setDistance((int) Math.round(distance));
        }

        stationVO.setStationType(stationType);
        stationVO.setCurrentType(null);
        stationVO.setMaxChargePower(null);
        stationVO.setPrice(null);
        stationVO.setRating(null);
        stationVO.setAvailableCount(privateStation.getMaxVehicles() - privateStation.getCurrentVehicles());

        return stationVO;
    }

    /**
     * 将PublicChargeStation转换为LongRoutePlanVO.StationVO
     */
    private LongRoutePlanVO.StationVO convertToLongRouteStationVO(PublicChargeStation publicStation,
            Double currentLng, Double currentLat,
            Integer stationType) {
        LongRoutePlanVO.StationVO stationVO = new LongRoutePlanVO.StationVO();

        stationVO.setStationId(publicStation.getId());
        stationVO.setStationName(publicStation.getName());
        stationVO.setLongitude(publicStation.getLongitude());
        stationVO.setLatitude(publicStation.getLatitude());
        stationVO.setAddress(publicStation.getLocationName());

        // 计算距离
        if (currentLng != null && currentLat != null && publicStation.getLongitude() != null
                && publicStation.getLatitude() != null) {
            double distance = GeoUtils.calculateDistance(currentLng, currentLat,
                    publicStation.getLongitude(), publicStation.getLatitude());
            stationVO.setDistance((int) Math.round(distance));
        }

        stationVO.setStationType(stationType);
        stationVO.setCurrentType(publicStation.getCurrentType());
        stationVO.setMaxChargePower(publicStation.getMaxChargePower());
        stationVO.setPrice(publicStation.getPrice());
        stationVO.setRating(publicStation.getRating());
        stationVO.setAvailableCount(publicStation.getMaxVehicles() - publicStation.getCurrentVehicles());

        return stationVO;
    }
}
