package com.baffang.admin.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baffang.admin.service.AmapService;
import com.baffang.common.exception.BusinessException;
import com.baffang.config.AmapProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmapServiceImpl implements AmapService {

    @Autowired
    private AmapProperties amapProperties;

    /**
     * 距离计算API地址
     */
    private static final String DISTANCE_API_URL = "https://restapi.amap.com/v3/distance";

    /**
     * 计算两点之间的驾车导航距离
     */
    @Override
    public Long getDrivingDistance(Double originLng, Double originLat, Double destLng, Double destLat) {
        try {
            // 构建请求URL
            // origins: 起点坐标（经度,纬度）
            // destination: 终点坐标（经度,纬度）
            // type: 1=驾车导航距离
            String url = String.format(
                    "%s?key=%s&origins=%s,%s&destination=%s,%s&type=1",
                    DISTANCE_API_URL,
                    amapProperties.getApiKey(),
                    originLng, originLat,
                    destLng, destLat);

            log.debug("调用高德距离API: {}", url);

            // 发送请求
            String response = HttpUtil.get(url, 5000);
            log.debug("高德API响应: {}", response);

            // 解析响应
            JSONObject result = JSON.parseObject(response);

            // 校验状态
            String status = result.getString("status");
            if (!"1".equals(status)) {
                String info = result.getString("info");
                log.error("高德API调用失败: status={}, info={}", status, info);
                throw new BusinessException("距离计算失败: " + info);
            }

            // 获取距离结果
            JSONArray results = result.getJSONArray("results");
            if (results == null || results.isEmpty()) {
                log.warn("高德API返回结果为空");
                return null;
            }

            JSONObject distanceResult = results.getJSONObject(0);
            String distanceStr = distanceResult.getString("distance");

            if (StringUtils.hasText(distanceStr)) {
                return Long.parseLong(distanceStr);
            }

            return null;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用高德距离API异常", e);
            throw new BusinessException("距离计算服务暂不可用");
        }
    }

    /**
     * 批量计算驾车导航距离
     * 高德API支持一次计算多个终点的距离（最多100个）
     */
    @Override
    public List<Long> getBatchDrivingDistance(Double originLng, Double originLat, List<double[]> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // 构建起点字符串（可以是多个起点，用|分隔）
            String originsStr = originLng + "," + originLat;

            // 构建终点字符串（第一个终点作为destination）
            // 注意：高德API的distance接口是计算多个起点到一个终点的距离
            // 如果要计算一个起点到多个终点，需要多次调用或使用路径规划API

            // 这里使用循环调用的方式
            List<Long> distances = new ArrayList<>();

            for (double[] dest : destinations) {
                try {
                    Long distance = getDrivingDistance(originLng, originLat, dest[0], dest[1]);
                    distances.add(distance);
                } catch (Exception e) {
                    log.warn("计算到点({},{})的距离失败: {}", dest[0], dest[1], e.getMessage());
                    distances.add(null);
                }
            }

            return distances;

        } catch (Exception e) {
            log.error("批量计算距离异常", e);
            throw new BusinessException("距离计算服务暂不可用");
        }
    }

    /**
     * 批量计算（优化版本 - 使用高德批量接口）
     * 高德distance接口支持：多个起点到一个终点
     * 如果要一个起点到多个终点，可以反过来调用
     */
    public List<Long> getBatchDrivingDistanceOptimized(Double originLng, Double originLat,
            List<double[]> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return new ArrayList<>();
        }

        // 如果终点数量较少，直接循环调用
        if (destinations.size() <= 5) {
            return getBatchDrivingDistance(originLng, originLat, destinations);
        }

        try {
            // 将多个"终点"作为"起点"，响应员位置作为"终点"
            // 这样可以一次API调用获取多个距离
            String originsStr = destinations.stream()
                    .map(dest -> dest[0] + "," + dest[1])
                    .collect(Collectors.joining("|"));

            String destStr = originLng + "," + originLat;

            String url = String.format(
                    "%s?key=%s&origins=%s&destination=%s&type=1",
                    DISTANCE_API_URL,
                    amapProperties.getApiKey(),
                    originsStr,
                    destStr);

            log.debug("调用高德批量距离API: origins数量={}", destinations.size());

            String response = HttpUtil.get(url, 10000);
            JSONObject result = JSON.parseObject(response);

            String status = result.getString("status");
            if (!"1".equals(status)) {
                String info = result.getString("info");
                log.error("高德API调用失败: {}", info);
                // 失败时回退到单个调用
                return getBatchDrivingDistance(originLng, originLat, destinations);
            }

            JSONArray results = result.getJSONArray("results");
            List<Long> distances = new ArrayList<>();

            if (results != null) {
                for (int i = 0; i < results.size(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    String distanceStr = item.getString("distance");
                    if (StringUtils.hasText(distanceStr)) {
                        distances.add(Long.parseLong(distanceStr));
                    } else {
                        distances.add(null);
                    }
                }
            }

            return distances;

        } catch (Exception e) {
            log.error("批量计算距离异常，回退到单个调用", e);
            return getBatchDrivingDistance(originLng, originLat, destinations);
        }
    }
}
