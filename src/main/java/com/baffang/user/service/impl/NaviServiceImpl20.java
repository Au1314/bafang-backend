package com.baffang.user.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baffang.common.entity.PublicChargeStation;
import com.baffang.config.AmapProperties;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.dto.LongRoutePlanDTO;
import com.baffang.user.dto.LongRoutePlanDTO20;
import com.baffang.user.dto.ShortRoutePlanDTO;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.AmapService;
import com.baffang.user.service.NaviService;
import com.baffang.user.vo.LongRoutePlanVO;
import com.baffang.user.vo.LongRoutePlanVO20;
import com.baffang.user.vo.ShortRoutePlanVO;
import com.baffang.user.vo.NaviPrivateStationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 导航服务实现类 2.0
 */
@Slf4j
@Service("naviService20")
public class NaviServiceImpl20 implements NaviService {

    // 默认值
    private static final int DEFAULT_VEHICLE_RANGE = 200;
    private static final int DEFAULT_TARGET_BATTERY = 80;
    private static final int BOTTOM_LINE_BATTERY = 20;
    private static final int DEFAULT_PREFER_STATION_TYPE = 1;
    private static final int DEFAULT_PREFER_TYPE = 1;

    @Autowired
    @Qualifier("userAmapService")
    private AmapService amapService;

    @Autowired
    private AmapProperties amapProperties;

    @Autowired
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    @Autowired
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    /**
     * 经纬度范围扩展值（用于数据库初步筛选）
     */
    private static final double LNG_LAT_RANGE = 0.3;

    @Override
    public Map<String, Object> searchPrivateStations(String keyword, Double currentLng, Double currentLat,
            Integer radius) {
        // 复用原有实现
        return null;
    }

    @Override
    public ShortRoutePlanVO planShortRoute(ShortRoutePlanDTO dto) {
        // 复用原有实现
        return null;
    }

    @Override
    public LongRoutePlanVO planLongRoute(LongRoutePlanDTO dto) {
        // 复用原有实现
        return null;
    }

    @Override
    public LongRoutePlanVO20 planLongRoute20(LongRoutePlanDTO20 dto) {
        // 1. 参数处理
        Integer vehicleRange = dto.getVehicleRange() != null ? dto.getVehicleRange() : DEFAULT_VEHICLE_RANGE;
        Integer currentBattery = dto.getCurrentBattery() != null ? dto.getCurrentBattery() : DEFAULT_TARGET_BATTERY;
        Integer bottomLineBattery = dto.getBottomLineBattery() != null ? dto.getBottomLineBattery()
                : BOTTOM_LINE_BATTERY;

        // 2. 初始化结果
        LongRoutePlanVO20 result = new LongRoutePlanVO20();
        List<LongRoutePlanVO20.ChargingStop> chargingStops = new ArrayList<>();

        // 3. 算法实现
        try {
            double currentLng = dto.getStartLng();
            double currentLat = dto.getStartLat();
            double endLng = dto.getEndLng();
            double endLat = dto.getEndLat();
            double currentBatteryPercent = currentBattery;

            int stopNumber = 1;
            double totalDistance = 0;
            double totalTime = 0;

            // 增加循环次数限制
            int maxLoopCount = 10;
            int loopCount = 0;

            // 记录已选充电桩，避免重复选择
            List<String> selectedStationIds = new ArrayList<>();

            // 主循环：直到到达终点附近
            while (loopCount < maxLoopCount) {
                loopCount++;
                // 计算当前点到终点的直线距离
                double straightDistanceToEnd = calculateDistance(currentLng, currentLat, endLng, endLat);

                // 检查是否已接近终点
                double safeRange = vehicleRange * 0.8; // 安全续航
                if (straightDistanceToEnd <= safeRange) {
                    // 可以直接到达终点
                    break;
                }

                // 1. 粗筛：筛选距离主线30~50km内的充电桩
                List<StationInfo> candidateStations = getCandidateStations(currentLng, currentLat, endLng, endLat);

                if (candidateStations.isEmpty()) {
                    // 兜底机制：扩大搜索范围
                    candidateStations = getCandidateStations(currentLng, currentLat, endLng, endLat, 80);
                    if (candidateStations.isEmpty()) {
                        result.setMessage("此路线无法充电抵达");
                        return result;
                    }
                }

                // 2. 预过滤：剔除与终点方向相反的充电桩
                List<StationInfo> filteredStations = filterStationsByDirection(currentLng, currentLat, endLng, endLat,
                        candidateStations);

                // 3. 排序：按直线距离终点越近排序
                filteredStations.sort((s1, s2) -> {
                    double dist1 = calculateDistance(s1.getLongitude(), s1.getLatitude(), endLng, endLat);
                    double dist2 = calculateDistance(s2.getLongitude(), s2.getLatitude(), endLng, endLat);
                    return Double.compare(dist1, dist2);
                });

                // 4. 精准验证：调用高德API验证可达性
                StationInfo bestStation = null;
                double actualDistance = 0;

                // 优化：优先验证公桩和有足够功率的充电桩
                List<StationInfo> priorityStations = filteredStations.stream()
                        .filter(station -> station.getStationType() == 1) // 优先公桩
                        .filter(station -> station.getMaxChargePower() != null && station.getMaxChargePower() >= 60000) // 优先功率≥60kW的充电桩
                        .filter(station -> !selectedStationIds.contains(station.getStationId())) // 排除已选充电桩
                        .limit(10) // 限制最多验证10个优先级充电桩
                        .collect(Collectors.toList());

                // 如果没有优先级充电桩，再验证其他充电桩
                if (priorityStations.isEmpty()) {
                    priorityStations = filteredStations.stream()
                            .filter(station -> !selectedStationIds.contains(station.getStationId())) // 排除已选充电桩
                            .limit(10) // 限制最多验证10个其他充电桩
                            .collect(Collectors.toList());
                }

                // 转换为批量查询格式
                List<double[]> destinations = priorityStations.stream()
                        .map(station -> new double[] { station.getLongitude(), station.getLatitude() })
                        .collect(Collectors.toList());

                // 调用批量距离查询API
                List<Long> distances = amapService.getBatchDrivingDistanceOptimized(currentLng, currentLat,
                        destinations);

                // 遍历结果找到最佳充电桩
                // 优先选择距离终点更近的充电桩
                double bestDistance = Double.MAX_VALUE;
                double bestStationDistanceToEnd = Double.MAX_VALUE;

                for (int i = 0; i < priorityStations.size(); i++) {
                    StationInfo station = priorityStations.get(i);
                    Long distance = distances.get(i);

                    if (distance != null) {
                        actualDistance = distance / 1000.0; // 转换为公里
                        if (actualDistance <= safeRange) {
                            // 计算充电桩到终点的距离
                            double stationDistanceToEnd = calculateDistance(station.getLongitude(),
                                    station.getLatitude(), endLng, endLat);

                            // 优先选择距离终点更近的充电桩
                            if (bestStation == null || stationDistanceToEnd < bestStationDistanceToEnd) {
                                bestStation = station;
                                bestDistance = actualDistance;
                                bestStationDistanceToEnd = stationDistanceToEnd;
                            }
                        }
                    }
                }

                if (bestStation == null) {
                    // 兜底机制：扩大搜索范围
                    candidateStations = getCandidateStations(currentLng, currentLat, endLng, endLat, 80);
                    if (candidateStations.isEmpty()) {
                        result.setMessage("此路线无法充电抵达");
                        return result;
                    }
                    // 再次尝试
                    // 优化：限制最多验证5个充电桩，减少API调用次数
                    List<StationInfo> limitedCandidateStations = candidateStations;
                    if (candidateStations.size() > 5) {
                        limitedCandidateStations = candidateStations.subList(0, 5);
                    }

                    // 增加两次批量查询之间的间隔控制
                    try {
                        TimeUnit.MILLISECONDS.sleep(500); // 0.5秒间隔
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("批量查询间隔睡眠被中断", e);
                    }

                    // 转换为批量查询格式
                    List<double[]> fallbackDestinations = limitedCandidateStations.stream()
                            .map(station -> new double[] { station.getLongitude(), station.getLatitude() })
                            .collect(Collectors.toList());

                    // 调用批量距离查询API
                    List<Long> fallbackDistances = amapService.getBatchDrivingDistanceOptimized(currentLng, currentLat,
                            fallbackDestinations);

                    // 遍历结果找到最佳充电桩
                    // 优先选择距离终点更近的充电桩
                    double fallbackBestDistance = Double.MAX_VALUE;
                    double fallbackBestStationDistanceToEnd = Double.MAX_VALUE;

                    for (int i = 0; i < limitedCandidateStations.size(); i++) {
                        StationInfo station = limitedCandidateStations.get(i);
                        Long distance = fallbackDistances.get(i);

                        if (distance != null) {
                            actualDistance = distance / 1000.0; // 转换为公里
                            if (actualDistance <= vehicleRange * 0.9) { // 放宽条件
                                // 计算充电桩到终点的距离
                                double stationDistanceToEnd = calculateDistance(station.getLongitude(),
                                        station.getLatitude(), endLng, endLat);

                                // 优先选择距离终点更近的充电桩
                                if (bestStation == null || stationDistanceToEnd < fallbackBestStationDistanceToEnd) {
                                    bestStation = station;
                                    fallbackBestDistance = actualDistance;
                                    fallbackBestStationDistanceToEnd = stationDistanceToEnd;
                                }
                            }
                        }
                    }
                    if (bestStation == null) {
                        result.setMessage("此路线无法充电抵达");
                        return result;
                    }
                }

                // 5. 计算到达时电量
                double arrivalBattery = currentBatteryPercent - (actualDistance / vehicleRange) * 100;
                arrivalBattery = Math.max(arrivalBattery, bottomLineBattery);

                // 6. 计算需要充电量
                double chargingAmount = 80 - arrivalBattery; // 充到80%
                chargingAmount = Math.max(chargingAmount, 0);

                // 7. 计算预计充电时间
                int estimatedChargingTime = 30; // 默认30分钟
                if (bestStation.getMaxChargePower() != null) {
                    estimatedChargingTime = (int) (chargingAmount * vehicleRange
                            / (bestStation.getMaxChargePower() / 1000.0 * 60));
                    estimatedChargingTime = Math.max(estimatedChargingTime, 10); // 最少10分钟
                }

                // 8. 创建充电停靠点
                LongRoutePlanVO20.ChargingStop stop = new LongRoutePlanVO20.ChargingStop();
                stop.setStopNumber(stopNumber++);

                LongRoutePlanVO20.StationVO stationVO = new LongRoutePlanVO20.StationVO();
                stationVO.setStationId(bestStation.getStationId());
                stationVO.setStationName(bestStation.getStationName());
                stationVO.setLongitude(bestStation.getLongitude());
                stationVO.setLatitude(bestStation.getLatitude());
                stationVO.setAddress(bestStation.getAddress());
                stationVO.setDistance((int) (actualDistance * 1000));
                stationVO.setStationType(bestStation.getStationType());
                stationVO.setCurrentType(bestStation.getCurrentType());
                stationVO.setMaxChargePower(bestStation.getMaxChargePower());
                stationVO.setPrice(bestStation.getPrice());
                stationVO.setRating(bestStation.getRating());
                stationVO.setAvailableCount(bestStation.getAvailableCount());
                stationVO.setEstimatedChargeTime(estimatedChargingTime);

                stop.setStation(stationVO);
                stop.setArrivalBattery(arrivalBattery);
                stop.setChargingAmount(chargingAmount);
                stop.setEstimatedChargingTime(estimatedChargingTime);
                stop.setDistanceFromPrev(actualDistance);
                stop.setDistanceToEnd(
                        calculateDistance(bestStation.getLongitude(), bestStation.getLatitude(), endLng, endLat));

                // 9. 搜索附近备选充电桩
                stop.setAlternativeStations(
                        getAlternativeStations(bestStation.getLongitude(), bestStation.getLatitude(),
                                bestStation.getStationId().toString()));

                // 10. 添加到停靠点列表
                chargingStops.add(stop);

                // 11. 更新状态
                totalDistance += actualDistance;
                totalTime += (actualDistance / 60) + (estimatedChargingTime / 60.0); // 假设平均时速60公里

                // 检查距离变化
                double previousDistance = calculateDistance(currentLng, currentLat, endLng, endLat);
                double newDistance = calculateDistance(bestStation.getLongitude(), bestStation.getLatitude(), endLng,
                        endLat);

                if (newDistance >= previousDistance) {
                    // 距离没有减少，说明选择的充电桩不合理
                    log.warn("选择的充电桩距离终点更远，尝试其他充电桩");
                    // 无法到达终点，直接返回
                    result.setMessage("此路线无法充电抵达");
                    return result;
                }

                // 记录已选充电桩
                selectedStationIds.add(bestStation.getStationId().toString());

                // 更新状态
                currentLng = bestStation.getLongitude();
                currentLat = bestStation.getLatitude();
                currentBatteryPercent = 80.0; // 充电到80%

                // 增加循环间隔控制，避免短时间内多次循环
                try {
                    TimeUnit.MILLISECONDS.sleep(50); // 0.05秒间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("循环间隔睡眠被中断", e);
                }
            }

            // 12. 计算最后一段距离
            double finalDistance = calculateDistance(currentLng, currentLat, endLng, endLat);
            totalDistance += finalDistance;
            totalTime += finalDistance / 60; // 假设平均时速60公里

            // 13. 填充结果
            result.setMessage("规划成功");
            result.setTotalDistance(totalDistance);
            result.setEstimatedTime(totalTime);
            result.setChargingStops(chargingStops);

        } catch (Exception e) {
            log.error("长途路径规划失败", e);
            result.setMessage("路径规划失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取候选充电桩
     */
    private List<StationInfo> getCandidateStations(double startLng, double startLat, double endLng, double endLat) {
        return getCandidateStations(startLng, startLat, endLng, endLat, 50);
    }

    /**
     * 获取候选充电桩（带搜索半径）
     */
    private List<StationInfo> getCandidateStations(double startLng, double startLat, double endLng, double endLat,
            double searchRadius) {
        List<StationInfo> stations = new ArrayList<>();

        // 计算经纬度范围
        double minLng = Math.min(startLng, endLng) - LNG_LAT_RANGE;
        double maxLng = Math.max(startLng, endLng) + LNG_LAT_RANGE;
        double minLat = Math.min(startLat, endLat) - LNG_LAT_RANGE;
        double maxLat = Math.max(startLat, endLat) + LNG_LAT_RANGE;

        // 查询公桩
        List<PublicChargeStation> publicStationList = userPublicChargeStationMapper
                .selectPublicStationsByGeoRange(minLng, maxLng, minLat, maxLat);
        if (publicStationList != null && !publicStationList.isEmpty()) {
            for (PublicChargeStation publicStation : publicStationList) {
                // 转换为公桩信息
                StationInfo stationInfo = new StationInfo(
                        publicStation.getId(),
                        publicStation.getName(),
                        publicStation.getLongitude(),
                        publicStation.getLatitude(),
                        publicStation.getLocationName(),
                        1, // 公桩类型
                        publicStation.getCurrentType(),
                        publicStation.getMaxChargePower(),
                        publicStation.getPrice(),
                        publicStation.getRating(),
                        publicStation.getMaxVehicles() - publicStation.getCurrentVehicles());
                stations.add(stationInfo);
            }
        }

        // 查询私桩
        List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                .selectPrivateStationsByGeoRange(null, minLng, maxLng, minLat, maxLat);
        if (privateStationList != null && !privateStationList.isEmpty()) {
            for (NaviPrivateStationVO privateStation : privateStationList) {
                // 转换为私桩信息
                StationInfo stationInfo = new StationInfo(
                        privateStation.getId(),
                        privateStation.getName(),
                        privateStation.getLongitude(),
                        privateStation.getLatitude(),
                        privateStation.getLocationName(),
                        0, // 私桩类型
                        null, // 私桩可能没有电流类型信息
                        null, // 私桩可能没有最大功率信息
                        null, // 私桩可能没有价格信息
                        null, // 私桩可能没有评分信息
                        privateStation.getMaxVehicles() - privateStation.getCurrentVehicles());
                stations.add(stationInfo);
            }
        }

        return stations;
    }

    /**
     * 过滤与终点方向相反的充电桩
     */
    private List<StationInfo> filterStationsByDirection(double startLng, double startLat, double endLng, double endLat,
            List<StationInfo> stations) {
        return stations.stream().filter(station -> {
            // 计算向量点积判断方向
            double vector1X = endLng - startLng;
            double vector1Y = endLat - startLat;
            double vector2X = station.getLongitude() - startLng;
            double vector2Y = station.getLatitude() - startLat;
            double dotProduct = vector1X * vector2X + vector1Y * vector2Y;
            return dotProduct > 0;
        }).collect(Collectors.toList());
    }

    /**
     * 获取附近备选充电桩
     */
    private List<LongRoutePlanVO20.StationVO> getAlternativeStations(double lng, double lat, String currentStationId) {
        List<LongRoutePlanVO20.StationVO> alternatives = new ArrayList<>();

        // 计算经纬度范围（附近3公里）
        double minLng = lng - 0.03;
        double maxLng = lng + 0.03;
        double minLat = lat - 0.03;
        double maxLat = lat + 0.03;

        // 查询公桩
        List<PublicChargeStation> publicStationList = userPublicChargeStationMapper
                .selectPublicStationsByGeoRange(minLng, maxLng, minLat, maxLat);
        if (publicStationList != null && !publicStationList.isEmpty()) {
            for (PublicChargeStation publicStation : publicStationList) {
                // 排除当前停靠点的充电桩
                if (publicStation.getId().toString().equals(currentStationId)) {
                    continue;
                }

                // 计算距离
                double distance = calculateDistance(lng, lat, publicStation.getLongitude(),
                        publicStation.getLatitude());
                if (distance <= 3) { // 3公里内
                    LongRoutePlanVO20.StationVO stationVO = new LongRoutePlanVO20.StationVO();
                    stationVO.setStationId(publicStation.getId());
                    stationVO.setStationName(publicStation.getName());
                    stationVO.setLongitude(publicStation.getLongitude());
                    stationVO.setLatitude(publicStation.getLatitude());
                    stationVO.setAddress(publicStation.getLocationName());
                    stationVO.setDistance((int) (distance * 1000)); // 转换为米
                    stationVO.setStationType(1);
                    stationVO.setCurrentType(publicStation.getCurrentType());
                    stationVO.setMaxChargePower(publicStation.getMaxChargePower());
                    stationVO.setPrice(publicStation.getPrice());
                    stationVO.setRating(publicStation.getRating());
                    stationVO.setAvailableCount(publicStation.getMaxVehicles() - publicStation.getCurrentVehicles());
                    alternatives.add(stationVO);
                }
            }
        }

        // 查询私桩
        List<NaviPrivateStationVO> privateStationList = userPrivateChargeStationMapper
                .selectPrivateStationsByGeoRange(null, minLng, maxLng, minLat, maxLat);
        if (privateStationList != null && !privateStationList.isEmpty()) {
            for (NaviPrivateStationVO privateStation : privateStationList) {
                // 排除当前停靠点的充电桩
                if (privateStation.getId().toString().equals(currentStationId)) {
                    continue;
                }

                // 计算距离
                double distance = calculateDistance(lng, lat, privateStation.getLongitude(),
                        privateStation.getLatitude());
                if (distance <= 3) { // 3公里内
                    LongRoutePlanVO20.StationVO stationVO = new LongRoutePlanVO20.StationVO();
                    stationVO.setStationId(privateStation.getId());
                    stationVO.setStationName(privateStation.getName());
                    stationVO.setLongitude(privateStation.getLongitude());
                    stationVO.setLatitude(privateStation.getLatitude());
                    stationVO.setAddress(privateStation.getLocationName());
                    stationVO.setDistance((int) (distance * 1000)); // 转换为米
                    stationVO.setStationType(0);
                    stationVO.setCurrentType(null);
                    stationVO.setMaxChargePower(null);
                    stationVO.setPrice(null);
                    stationVO.setRating(null);
                    stationVO.setAvailableCount(privateStation.getMaxVehicles() - privateStation.getCurrentVehicles());
                    alternatives.add(stationVO);
                }
            }
        }

        // 限制返回最多3个备选充电桩
        if (alternatives.size() > 3) {
            return alternatives.subList(0, 3);
        }

        return alternatives;
    }

    /**
     * 计算两点之间的直线距离（公里）
     */
    private double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        final double R = 6371.0; // 地球半径（公里）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * 充电桩信息内部类
     */
    @lombok.Data
    private static class StationInfo {
        private Integer stationId;
        private String stationName;
        private Double longitude;
        private Double latitude;
        private String address;
        private Integer stationType;
        private Integer currentType;
        private Integer maxChargePower;
        private Double price;
        private Double rating;
        private Integer availableCount;

        public StationInfo(Integer stationId, String stationName, Double longitude, Double latitude,
                String address, Integer stationType, Integer currentType,
                Integer maxChargePower, Double price, Double rating, Integer availableCount) {
            this.stationId = stationId;
            this.stationName = stationName;
            this.longitude = longitude;
            this.latitude = latitude;
            this.address = address;
            this.stationType = stationType;
            this.currentType = currentType;
            this.maxChargePower = maxChargePower;
            this.price = price;
            this.rating = rating;
            this.availableCount = availableCount;
        }
    }
}