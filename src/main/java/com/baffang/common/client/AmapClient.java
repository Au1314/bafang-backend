package com.baffang.common.client;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baffang.user.vo.ChargingStationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AmapClient {
    
    @Value("${amap.api.key:}")
    private String apiKey;
    
    @Value("${amap.api.url.poi-search:https://restapi.amap.com/v3/place/text}")
    private String poiSearchUrl;
    
    @Value("${amap.search.keywords:充电站}")
    private String keywords;
    
    @Value("${amap.search.default-radius:5000}")
    private Integer defaultRadius;
    
    @PostConstruct
    public void init() {
        log.info("========== AmapClient 初始化 ==========");
        log.info("API Key: {}", apiKey);
        log.info("POI搜索URL: {}", poiSearchUrl);
        log.info("搜索关键词: {}", keywords);
        log.info("默认半径: {}米", defaultRadius);
        log.info("=====================================");
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("高德地图API Key未配置，充电桩功能将不可用。请在配置文件中设置 amap.api.key");
        }
    }
    
    /**
     * 搜索附近的充电站
     */
    public List<ChargingStationVO> searchChargingStations(
            Double longitude, Double latitude, Integer radius, Integer page, Integer pageSize) {

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("高德地图API Key未配置，无法搜索充电站");
            return new ArrayList<>();
        }

        // 打印请求参数
        log.info("========== 开始搜索充电站 ==========");
        log.info("用户位置: 经度={}, 纬度={}", longitude, latitude);
        
        String location = longitude + "," + latitude;
        int searchRadius = radius != null ? radius : defaultRadius;
        int currentPage = page != null ? page : 1;
        int size = pageSize != null ? pageSize : 20;
        
        log.info("搜索半径: {}米", searchRadius);
        log.info("关键词: {}", keywords);
        log.info("页码: {}, 每页数量: {}", currentPage, size);
        
        try {
            // 使用 Hutool 构建请求
            HttpResponse response = HttpRequest.get(poiSearchUrl)
                    .timeout(10000)
                    .form("key", apiKey)
                    .form("keywords", keywords)
                    .form("location", location)
                    .form("radius", searchRadius)
                    .form("extensions", "all")
                    .form("offset", size)
                    .form("page", currentPage)
                    .execute();

            log.info("HTTP响应状态码: {}", response.getStatus());
            
            if (response.isOk()) {
                String responseBody = response.body();
                log.info("API响应内容: {}", responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody);
                
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                String status = jsonObject.getString("status");
                String info = jsonObject.getString("info");
                String count = jsonObject.getString("count");
                
                log.info("API返回 - status: {}, info: {}, 找到数量: {}", status, info, count);
                
                if ("1".equals(status)) {
                    List<ChargingStationVO> stations = parseStations(jsonObject, longitude, latitude);
                    log.info("成功解析到 {} 个充电站", stations.size());
                    return stations;
                } else {
                    log.error("API调用失败: {}", info);
                }
            } else {
                log.error("HTTP请求失败，状态码: {}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("调用高德API异常", e);
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 解析充电站数据
     */
    /**
 * 解析充电站数据
 */
private List<ChargingStationVO> parseStations(JSONObject jsonObject, Double userLon, Double userLat) {
    List<ChargingStationVO> stations = new ArrayList<>();
    JSONArray pois = jsonObject.getJSONArray("pois");
    
    if (pois != null && !pois.isEmpty()) {
        log.info("开始解析 {} 个充电站数据", pois.size());
        
        for (int i = 0; i < pois.size(); i++) {
            JSONObject poi = pois.getJSONObject(i);
            
            try {
                String location = poi.getString("location");
                if (location != null && !location.isEmpty()) {
                    String[] loc = location.split(",");
                    Double stationLon = Double.parseDouble(loc[0]);
                    Double stationLat = Double.parseDouble(loc[1]);
                    
                    double distance = calculateDistance(userLat, userLon, stationLat, stationLon);
                    
                    // ========== 解析新增字段 ==========
                    
                    // 1. POI类型
                    String poiType = poi.getString("type");
                    
                    // 2. 评分和营业时间（从 biz_ext 中获取）
                    JSONObject bizExt = poi.getJSONObject("biz_ext");
                    Double rating = null;
                    String openTime = null;
                    if (bizExt != null) {
                        String ratingStr = bizExt.getString("rating");
                        if (ratingStr != null && !ratingStr.isEmpty()) {
                            try {
                                rating = Double.parseDouble(ratingStr);
                            } catch (NumberFormatException e) {
                                log.warn("解析评分失败: {}", ratingStr);
                            }
                        }
                        openTime = bizExt.getString("opentime2");
                        if (openTime == null || openTime.isEmpty()) {
                            openTime = bizExt.getString("open_time");
                        }
                    }
                    
                    // 3. 地理位置信息
                    String province = poi.getString("pname");
                    String city = poi.getString("cityname");
                    String district = poi.getString("adname");
                    
                    // 4. 照片列表
                    List<String> photos = new ArrayList<>();
                    JSONArray photosArray = poi.getJSONArray("photos");
                    if (photosArray != null && !photosArray.isEmpty()) {
                        for (int j = 0; j < photosArray.size(); j++) {
                            JSONObject photo = photosArray.getJSONObject(j);
                            String photoUrl = photo.getString("url");
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                photos.add(photoUrl);
                            }
                        }
                    }
                    
                    // 构建充电站对象
                    ChargingStationVO station = ChargingStationVO.builder()
                            .id(poi.getString("id"))
                            .name(poi.getString("name"))
                            .address(poi.getString("address"))
                            .longitude(stationLon)
                            .latitude(stationLat)
                            .distance(distance)
                            .chargeFee(extractChargeFee(poi))
                            .availablePiles(extractAvailablePiles(poi))
                            .totalPiles(extractTotalPiles(poi))
                            .businessHours(poi.getString("business_area"))
                            .tel(poi.getString("tel"))
                            .type(poi.getString("type"))
                            .parkingFee(extractParkingFee(poi))
                            // 新增字段
                            .poiType(poiType)
                            .rating(rating)
                            .openTime(openTime)
                            .province(province)
                            .city(city)
                            .district(district)
                            .photos(photos)
                            .build();
                    
                    stations.add(station);
                    log.debug("解析充电站: {} - 距离: {}公里, 评分: {}", 
                            poi.getString("name"), distance, rating);
                }
            } catch (Exception e) {
                log.error("解析单个充电站数据失败: {}", e.getMessage());
            }
        }
    } else {
        log.warn("API返回的充电站列表为空");
    }
    
    stations.sort((s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()));
    log.info("按距离排序完成，最近充电站: {}，距离: {}公里", 
            stations.isEmpty() ? "无" : stations.get(0).getName(),
            stations.isEmpty() ? 0 : stations.get(0).getDistance());
    
    return stations;
}
    
    /**
     * 计算两点间距离（公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    private String extractChargeFee(JSONObject poi) {
        JSONObject business = poi.getJSONObject("business");
        if (business != null) {
            String chargeFee = business.getString("charge_fee");
            if (chargeFee != null && !chargeFee.isEmpty()) {
                return chargeFee;
            }
        }
        
        String name = poi.getString("name");
        if (name != null) {
            String fee = ReUtil.get("(\\d+\\.?\\d*)元", name, 1);
            if (fee != null) {
                return fee + "元/度";
            }
        }
        return "请现场咨询";
    }
    
    private Integer extractAvailablePiles(JSONObject poi) {
        JSONObject business = poi.getJSONObject("business");
        if (business != null) {
            String businessInfo = business.getString("business_info");
            if (businessInfo != null) {
                String num = ReUtil.get("空闲(\\d+)个", businessInfo, 1);
                if (num != null) {
                    return Integer.parseInt(num);
                }
            }
        }
        return null;
    }
    
    private Integer extractTotalPiles(JSONObject poi) {
        JSONObject business = poi.getJSONObject("business");
        if (business != null) {
            String businessInfo = business.getString("business_info");
            if (businessInfo != null) {
                String num = ReUtil.get("共(\\d+)个", businessInfo, 1);
                if (num != null) {
                    return Integer.parseInt(num);
                }
            }
        }
        return null;
    }
    
    private String extractParkingFee(JSONObject poi) {
        JSONObject business = poi.getJSONObject("business");
        if (business != null) {
            String parkingFee = business.getString("parking_fee");
            if (parkingFee != null && !parkingFee.isEmpty()) {
                return parkingFee;
            }
        }
        return "请现场咨询";
    }
}