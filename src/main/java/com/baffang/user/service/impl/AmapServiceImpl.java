package com.baffang.user.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baffang.config.AmapProperties;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.service.AmapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("userAmapService")
public class AmapServiceImpl implements AmapService {

    @Autowired
    private AmapProperties amapProperties;

    /**
     * 距离计算API地址
     */
    private static final String DISTANCE_API_URL = "https://restapi.amap.com/v3/distance";

    /**
     * 批量查询最小间隔时间（毫秒）
     */
    private static final long BATCH_QUERY_MIN_INTERVAL = 500; // 0.5秒

    /**
     * 上次批量查询时间
     */
    private long lastBatchQueryTime = 0;

    /**
     * QPS超限重试次数
     */
    private static final int QPS_EXCEEDED_RETRY_MAX = 3;

    /**
     * QPS超限重试初始间隔（毫秒）
     */
    private static final long QPS_EXCEEDED_RETRY_INITIAL_INTERVAL = 1000; // 1秒

    /**
     * 熔断状态
     */
    private boolean isCircuitBreakerOpen = false;

    /**
     * 熔断开始时间
     */
    private long circuitBreakerStartTime = 0;

    /**
     * 熔断持续时间（毫秒）
     */
    private static final long CIRCUIT_BREAKER_DURATION = 30000; // 30秒

    /**
     * 计算两点之间的驾车导航距离
     */
    @Override
    public Long getDrivingDistance(Double originLng, Double originLat, Double destLng, Double destLat) {
        // 检查熔断状态
        if (isCircuitBreakerOpen) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - circuitBreakerStartTime < CIRCUIT_BREAKER_DURATION) {
                log.warn("熔断状态中，拒绝调用高德API");
                throw new BusinessException("距离计算服务暂不可用");
            } else {
                // 熔断超时，恢复正常
                isCircuitBreakerOpen = false;
                log.info("熔断状态已恢复");
            }
        }

        int retryCount = 0;
        while (retryCount <= QPS_EXCEEDED_RETRY_MAX) {
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

                    // 处理QPS超限
                    if ("CUQPS_HAS_EXCEEDED_THE_LIMIT".equals(info)) {
                        retryCount++;
                        if (retryCount > QPS_EXCEEDED_RETRY_MAX) {
                            // 开启熔断
                            isCircuitBreakerOpen = true;
                            circuitBreakerStartTime = System.currentTimeMillis();
                            log.error("QPS超限次数过多，开启熔断");
                            throw new BusinessException("距离计算失败: " + info);
                        }

                        // 指数退避重试
                        long retryInterval = QPS_EXCEEDED_RETRY_INITIAL_INTERVAL * (long) Math.pow(2, retryCount - 1);
                        log.warn("QPS超限，{}毫秒后重试（第{}次）", retryInterval, retryCount);
                        TimeUnit.MILLISECONDS.sleep(retryInterval);
                        continue;
                    }

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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("重试睡眠被中断", e);
                throw new BusinessException("距离计算服务暂不可用");
            } catch (Exception e) {
                log.error("调用高德距离API异常", e);
                throw new BusinessException("距离计算服务暂不可用");
            }
        }

        throw new BusinessException("距离计算服务暂不可用");
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
    @Override
    public List<Long> getBatchDrivingDistanceOptimized(Double originLng, Double originLat,
            List<double[]> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return new ArrayList<>();
        }

        // 如果只有1个终点，直接调用单个查询
        if (destinations.size() == 1) {
            return getBatchDrivingDistance(originLng, originLat, destinations);
        }

        try {
            // 控制批量查询间隔时间
            long currentTime = System.currentTimeMillis();
            long timeSinceLastQuery = currentTime - lastBatchQueryTime;

            // 动态调整间隔时间：如果最近有QPS超限错误，增加间隔时间
            long dynamicInterval = BATCH_QUERY_MIN_INTERVAL;
            if (isCircuitBreakerOpen) {
                // 熔断状态中，增加间隔时间
                dynamicInterval = 2000; // 2秒
            } else if (currentTime - circuitBreakerStartTime < 60000) {
                // 熔断恢复后1分钟内，增加间隔时间
                dynamicInterval = 1000; // 1秒
            }

            if (timeSinceLastQuery < dynamicInterval) {
                try {
                    TimeUnit.MILLISECONDS.sleep(dynamicInterval - timeSinceLastQuery);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("批量查询间隔睡眠被中断", e);
                }
            }
            lastBatchQueryTime = System.currentTimeMillis();

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

    /**
     * 驾车路线规划
     */
    @Override
    public JSONObject getDrivingRoute(Double originLng, Double originLat, Double destLng, Double destLat,
            Integer strategy, String waypoints) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v5/direction/driving");
            urlBuilder.append("?key=").append(amapProperties.getApiKey());
            urlBuilder.append("&origin=").append(originLng).append(",").append(originLat);
            urlBuilder.append("&destination=").append(destLng).append(",").append(destLat);

            // 添加可选参数
            if (strategy != null) {
                urlBuilder.append("&strategy=").append(strategy);
            }

            if (StringUtils.hasText(waypoints)) {
                urlBuilder.append("&waypoints=").append(waypoints);
            }

            urlBuilder.append("&output=json");

            String url = urlBuilder.toString();
            log.debug("调用高德驾车路线规划API: {}", url);

            // 发送请求
            String response = HttpUtil.get(url, 10000);
            log.debug("高德API响应: {}", response);

            // 解析响应
            JSONObject result = JSON.parseObject(response);

            // 校验状态
            String status = result.getString("status");
            if (!"1".equals(status)) {
                String info = result.getString("info");
                log.error("高德API调用失败: status={}, info={}", status, info);
                throw new BusinessException("路线规划失败: " + info);
            }

            return result;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用高德路线规划API异常", e);
            throw new BusinessException("路线规划服务暂不可用");
        }
    }
}