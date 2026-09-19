package com.baffang.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 地理计算工具类
 * 支持经纬度距离计算、按距离半径筛选坐标点等核心功能
 * 适配充电桩搜索、V2V呼叫范围、导航路线规划等业务场景
 */
public class GeoUtils {

    // 地球平均半径(米)，采用WGS84坐标系标准值
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 将角度值转换为弧度值
     * 
     * @param degree 角度值
     * @return 弧度值
     */
    private static double toRadians(double degree) {
        return Math.toRadians(degree);
    }

    /**
     * 计算两个经纬度坐标之间的直线距离（米）
     * 使用Haversine公式，适用于中短距离计算，精度满足充电桩/车辆导航场景
     * 
     * @param lon1 第一个点的经度
     * @param lat1 第一个点的纬度
     * @param lon2 第二个点的经度
     * @param lat2 第二个点的纬度
     * @return 两点之间的距离（米），保留2位小数
     */
    public static double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        // 校验经纬度合法性
        if (!isValidLatitude(lat1) || !isValidLatitude(lat2)
                || !isValidLongitude(lon1) || !isValidLongitude(lon2)) {
            throw new IllegalArgumentException("经纬度格式不合法，纬度范围[-90,90]，经度范围[-180,180]");
        }

        // 转换为弧度
        double radLat1 = toRadians(lat1);
        double radLat2 = toRadians(lat2);
        double radLon1 = toRadians(lon1);
        double radLon2 = toRadians(lon2);

        // 计算纬度差和经度差
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        // Haversine公式核心计算
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                        * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离并保留2位小数
        double distance = EARTH_RADIUS * c;
        return Math.round(distance * 100) / 100.0;
    }

    /**
     * 从坐标列表中筛选出指定中心点、指定半径范围内的坐标点
     * 适用于"附近充电桩搜索"、"V2V呼叫范围筛选"等场景
     * 
     * @param centerLon 中心点经度
     * @param centerLat 中心点纬度
     * @param radius    搜索半径（米）
     * @param points    待筛选的坐标点列表
     * @return 半径范围内的坐标点列表
     */
    public static List<GeoPoint> searchPointsInRadius(double centerLon, double centerLat,
            double radius, List<GeoPoint> points) {
        // 参数校验
        if (radius <= 0) {
            throw new IllegalArgumentException("搜索半径必须大于0");
        }
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }

        List<GeoPoint> result = new ArrayList<>();
        for (GeoPoint point : points) {
            double distance = calculateDistance(centerLon, centerLat,
                    point.getLongitude(), point.getLatitude());
            // 距离小于等于半径则加入结果集
            if (distance <= radius) {
                point.setDistance(distance); // 记录该点到中心点的距离
                result.add(point);
            }
        }
        return result;
    }

    /**
     * 校验纬度合法性（范围：-90 ~ 90）
     * 
     * @param latitude 纬度值
     * @return 合法返回true，否则false
     */
    public static boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    /**
     * 校验经度合法性（范围：-180 ~ 180）
     * 
     * @param longitude 经度值
     * @return 合法返回true，否则false
     */
    public static boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

    /**
     * 经纬度坐标实体类
     * 封装经度、纬度，以及可选的距离（用于搜索结果）
     */
    public static class GeoPoint {
        private double longitude; // 经度
        private double latitude; // 纬度
        private double distance; // 到中心点的距离（米），仅搜索场景有效

        // 空构造
        public GeoPoint() {
        }

        // 带参构造
        public GeoPoint(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        // getter & setter
        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "GeoPoint{" +
                    "longitude=" + longitude +
                    ", latitude=" + latitude +
                    ", distance=" + distance + "m" +
                    '}';
        }
    }

    // 测试示例

}