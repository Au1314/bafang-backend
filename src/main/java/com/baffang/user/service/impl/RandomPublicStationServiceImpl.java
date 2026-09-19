package com.baffang.user.service.impl;

import com.baffang.common.entity.PublicChargeStation;
import com.baffang.user.dto.RandomPublicStationDTO;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.RandomPublicStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 公桩随机生成服务实现类
 */
@Service
public class RandomPublicStationServiceImpl implements RandomPublicStationService {

    @Autowired
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    private static final Random random = new Random();

    // 公桩名称前缀列表
    private static final String[] STATION_NAME_PREFIXES = { "快充站", "充电桩", "充电站", "充电点", "电力站", "能源站", "电桩站", "充电服务站",
            "绿色能源站", "智能充电站" };

    // 公桩名称后缀列表
    private static final String[] STATION_NAME_SUFFIXES = { "A区", "B区", "中心站", "服务站", "便民点", "旗舰站", "站点", "分部", "网点",
            "停车区", "加油充电一体站", "综合服务站" };

    // 地点名称 - 区域列表
    private static final String[] LOCATION_REGIONS = { "朝阳区", "海淀区", "东城区", "西城区", "丰台区", "石景山区", "通州区", "顺义区", "昌平区",
            "大兴区", "房山区", "门头沟区", "怀柔区", "平谷区", "密云区", "延庆区" };

    // 地点名称 - 场所列表
    private static final String[] LOCATION_PLACES = { "人民广场", "科技园区", "商业中心", "居民小区", "医院", "学校", "酒店", "购物中心", "公园",
            "地铁站", "火车站", "汽车站", "机场", "体育中心", "文化广场", "政务中心", "物流园", "工业园", "创业园", "旅游景点" };

    // 地点名称 - 道路列表
    private static final String[] LOCATION_ROADS = { "长安街", "中关村大街", "王府井大街", "西单大街", "东三环", "西三环", "北四环", "南四环", "建国路",
            "复兴路", "朝阳路", "朝阳北路", "京顺路", "京承高速", "京藏高速", "京沪高速" };

    // 公桩建设者列表
    private static final String[] PROVIDERS = { "星星充电", "特来电", "国家电网", "南方电网", "比亚迪", "特斯拉", "蔚来", "小鹏", "理想", "吉利" };

    // 公桩描述列表
    private static final String[] DESCRIPTIONS = { "24小时营业", "支持快充", "环境舒适", "停车方便", "服务周到", "设备先进", "价格实惠", "位置优越",
            "充电速度快", "安全可靠" };

    @Override
    public List<PublicChargeStation> generatePublicStationsInRadius(double centerLongitude, double centerLatitude,
            double radius, int count) {
        List<PublicChargeStation> stations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 生成半径范围内的随机经纬度
            double[] randomPoint = generateRandomPointInRadius(centerLongitude, centerLatitude, radius);
            double longitude = randomPoint[0];
            double latitude = randomPoint[1];

            PublicChargeStation station = generateRandomPublicStation();
            station.setLongitude(longitude);
            station.setLatitude(latitude);
            stations.add(station);
        }

        // 批量插入数据库
        if (!stations.isEmpty()) {
            userPublicChargeStationMapper.batchInsertPublicStations(stations);
        }

        return stations;
    }

    @Override
    public List<PublicChargeStation> generatePublicStationsInArea(double startLongitude, double startLatitude,
            double endLongitude, double endLatitude, int count) {
        List<PublicChargeStation> stations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 生成区域内的随机经纬度
            double longitude = startLongitude + random.nextDouble() * (endLongitude - startLongitude);
            double latitude = startLatitude + random.nextDouble() * (endLatitude - startLatitude);

            PublicChargeStation station = generateRandomPublicStation();
            station.setLongitude(longitude);
            station.setLatitude(latitude);
            stations.add(station);
        }

        // 批量插入数据库
        if (!stations.isEmpty()) {
            userPublicChargeStationMapper.batchInsertPublicStations(stations);
        }

        return stations;
    }

    @Override
    public PublicChargeStation generateSinglePublicStation(RandomPublicStationDTO dto) {
        PublicChargeStation station = generateRandomPublicStation();

        // 使用DTO中指定的字段值
        if (dto.getName() != null) {
            station.setName(dto.getName());
        }
        if (dto.getLocationName() != null) {
            station.setLocationName(dto.getLocationName());
        }
        if (dto.getLongitude() != null) {
            station.setLongitude(dto.getLongitude());
        }
        if (dto.getLatitude() != null) {
            station.setLatitude(dto.getLatitude());
        }
        if (dto.getCurrentType() != null) {
            station.setCurrentType(dto.getCurrentType());
        }
        if (dto.getPrice() != null) {
            station.setPrice(dto.getPrice());
        }
        if (dto.getMaxChargeVoltage() != null) {
            station.setMaxChargeVoltage(dto.getMaxChargeVoltage());
        }
        if (dto.getMaxChargeCurrent() != null) {
            station.setMaxChargeCurrent(dto.getMaxChargeCurrent());
        }
        if (dto.getMaxChargePower() != null) {
            station.setMaxChargePower(dto.getMaxChargePower());
        }
        if (dto.getMaxVehicles() != null) {
            station.setMaxVehicles(dto.getMaxVehicles());
        }
        if (dto.getCurrentVehicles() != null) {
            station.setCurrentVehicles(dto.getCurrentVehicles());
        }
        if (dto.getOpenStatus() != null) {
            station.setOpenStatus(dto.getOpenStatus());
        }
        if (dto.getProvider() != null) {
            station.setProvider(dto.getProvider());
        }
        if (dto.getImage() != null) {
            station.setImage(dto.getImage());
        }
        if (dto.getDescription() != null) {
            station.setDescription(dto.getDescription());
        }
        if (dto.getRating() != null) {
            station.setRating(dto.getRating());
        }

        // 插入数据库
        userPublicChargeStationMapper.insertPublicStation(station);

        return station;
    }

    /**
     * 生成随机公桩信息
     */
    private PublicChargeStation generateRandomPublicStation() {
        PublicChargeStation station = new PublicChargeStation();

        // 生成随机名称
        station.setName(generateRandomName());

        // 生成随机地点名称
        station.setLocationName(generateRandomLocationName());

        // 生成随机电流类型（0：直流电，1：交流电）
        station.setCurrentType(random.nextInt(2));

        // 生成随机价格（0.5-3.0元/度，保留2位小数）
        station.setPrice(Math.round((0.5 + random.nextDouble() * 2.5) * 100.0) / 100.0);

        // 生成随机最大充电电压（220-1000伏特）
        station.setMaxChargeVoltage(220 + random.nextInt(781));

        // 生成随机最大充电电流（10-300安培）
        station.setMaxChargeCurrent(10 + random.nextInt(291));

        // 生成随机最大充电功率（2200-300000瓦）
        station.setMaxChargePower(2200 + random.nextInt(297801));

        // 生成随机最大车辆数（1-20辆）
        station.setMaxVehicles(1 + random.nextInt(20));

        // 生成随机当前车辆数（0-最大车辆数）
        station.setCurrentVehicles(random.nextInt(station.getMaxVehicles() + 1));

        // 生成随机开放状态（0：关闭，1：开启）
        station.setOpenStatus(random.nextInt(2));

        // 生成随机公桩建设者
        station.setProvider(generateRandomProvider());

        // 生成随机演示图片（占位符）
        station.setImage("https://example.com/station_image_" + (random.nextInt(10) + 1) + ".jpg");

        // 生成随机描述信息
        station.setDescription(generateRandomDescription());

        // 生成随机评分（0-5分，保留1位小数）
        station.setRating(Math.round((random.nextDouble() * 5.0) * 10.0) / 10.0);

        // 设置更新时间
        station.setUpdateTime(new Date());

        return station;
    }

    /**
     * 生成随机公桩名称
     */
    private String generateRandomName() {
        String prefix = STATION_NAME_PREFIXES[random.nextInt(STATION_NAME_PREFIXES.length)];
        String suffix = STATION_NAME_SUFFIXES[random.nextInt(STATION_NAME_SUFFIXES.length)];
        int number = random.nextInt(1000); // 增加数字范围到0-999

        // 随机选择名称模式
        int mode = random.nextInt(4);
        switch (mode) {
            case 0: // 前缀+数字+后缀
                return prefix + number + suffix;
            case 1: // 前缀+后缀
                return prefix + suffix;
            case 2: // 数字+前缀+后缀
                return number + prefix + suffix;
            case 3: // 前缀+双数字+后缀
                return prefix + String.format("%02d", number % 100) + suffix;
            default:
                return prefix + number + suffix;
        }
    }

    /**
     * 生成随机地点名称
     */
    private String generateRandomLocationName() {
        // 随机选择地点名称模式
        int mode = random.nextInt(5);
        switch (mode) {
            case 0: // 区域+场所
                return LOCATION_REGIONS[random.nextInt(LOCATION_REGIONS.length)]
                        + LOCATION_PLACES[random.nextInt(LOCATION_PLACES.length)];
            case 1: // 场所+区域
                return LOCATION_PLACES[random.nextInt(LOCATION_PLACES.length)]
                        + LOCATION_REGIONS[random.nextInt(LOCATION_REGIONS.length)];
            case 2: // 区域+道路
                return LOCATION_REGIONS[random.nextInt(LOCATION_REGIONS.length)]
                        + LOCATION_ROADS[random.nextInt(LOCATION_ROADS.length)];
            case 3: // 道路+数字
                return LOCATION_ROADS[random.nextInt(LOCATION_ROADS.length)] + (random.nextInt(1000) + 1) + "号";
            case 4: // 单一场所
                return LOCATION_PLACES[random.nextInt(LOCATION_PLACES.length)];
            default:
                return LOCATION_REGIONS[random.nextInt(LOCATION_REGIONS.length)]
                        + LOCATION_PLACES[random.nextInt(LOCATION_PLACES.length)];
        }
    }

    /**
     * 生成随机公桩建设者
     */
    private String generateRandomProvider() {
        return PROVIDERS[random.nextInt(PROVIDERS.length)];
    }

    /**
     * 生成随机描述信息
     */
    private String generateRandomDescription() {
        return DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];
    }

    /**
     * 在指定半径范围内生成随机经纬度
     * 
     * @param centerLongitude 中心点经度
     * @param centerLatitude  中心点纬度
     * @param radius          半径（米）
     * @return 随机经纬度 [longitude, latitude]
     */
    private double[] generateRandomPointInRadius(double centerLongitude, double centerLatitude, double radius) {
        // 地球半径（米）
        final double EARTH_RADIUS = 6371000;

        // 生成随机角度（0-360度）
        double angle = random.nextDouble() * 2 * Math.PI;

        // 生成随机距离（0-半径）
        double distance = Math.sqrt(random.nextDouble()) * radius;

        // 计算纬度偏移量（弧度）
        double latOffset = distance / EARTH_RADIUS;

        // 计算经度偏移量（弧度）
        double lonOffset = distance / (EARTH_RADIUS * Math.cos(Math.toRadians(centerLatitude)));

        // 计算随机经纬度
        double randomLongitude = centerLongitude + Math.toDegrees(lonOffset);
        double randomLatitude = centerLatitude + Math.toDegrees(latOffset);

        return new double[] { randomLongitude, randomLatitude };
    }
}