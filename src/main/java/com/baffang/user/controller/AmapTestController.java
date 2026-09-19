package com.baffang.user.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baffang.common.result.Result;
import com.baffang.user.service.AmapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 高德地图测试控制器
 */
@RestController
@RequestMapping("/api/v1/user/amap")
@Api(tags = "高德地图测试")
@Slf4j
public class AmapTestController {

    @Autowired
    @Qualifier("userAmapService")
    private AmapService amapService;

    /**
     * 测试驾车路线规划
     * 
     * @param originLng 起点经度
     * @param originLat 起点纬度
     * @param destLng   终点经度
     * @param destLat   终点纬度
     * @param strategy  驾车算路策略（可选）
     * @param waypoints 途经点（可选，格式："lng1,lat1;lng2,lat2"）
     * @return 路线规划结果
     */
    @GetMapping("/route")
    @ApiOperation("测试驾车路线规划")
    public Result<JSONObject> testDrivingRoute(
            @ApiParam("起点经度") @RequestParam Double originLng,
            @ApiParam("起点纬度") @RequestParam Double originLat,
            @ApiParam("终点经度") @RequestParam Double destLng,
            @ApiParam("终点纬度") @RequestParam Double destLat,
            @ApiParam("驾车算路策略") @RequestParam(required = false) Integer strategy,
            @ApiParam("途经点，格式：lng1,lat1;lng2,lat2") @RequestParam(required = false) String waypoints) {

        JSONObject result = amapService.getDrivingRoute(originLng, originLat, destLng, destLat, strategy, waypoints);
        return Result.success(result);
    }

    /**
     * 测试批量距离查询
     * 
     * @param originLng    起点经度
     * @param originLat    起点纬度
     * @param destinations 终点列表（格式："lng1,lat1|lng2,lat2|lng3,lat3"）
     * @return 距离列表
     */
    @GetMapping("/batch-distance")
    @ApiOperation("测试批量距离查询")
    public Result<List<Long>> testBatchDistance(
            @ApiParam("起点经度") @RequestParam Double originLng,
            @ApiParam("起点纬度") @RequestParam Double originLat,
            @ApiParam("终点列表，格式：lng1,lat1|lng2,lat2|lng3,lat3") @RequestParam String destinations) {

        // 解析终点列表
        List<double[]> destList = new ArrayList<>();
        String[] destArray = destinations.split("\\|");
        for (String dest : destArray) {
            String[] lngLat = dest.split(",");
            if (lngLat.length == 2) {
                try {
                    double lng = Double.parseDouble(lngLat[0]);
                    double lat = Double.parseDouble(lngLat[1]);
                    destList.add(new double[] { lng, lat });
                } catch (NumberFormatException e) {
                    log.warn("解析坐标失败: {}", dest);
                }
            }
        }

        List<Long> distances = amapService.getBatchDrivingDistanceOptimized(originLng, originLat, destList);
        return Result.success(distances);
    }
}
