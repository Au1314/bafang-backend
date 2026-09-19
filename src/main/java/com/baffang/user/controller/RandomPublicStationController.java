package com.baffang.user.controller;

import com.baffang.common.entity.PublicChargeStation;
import com.baffang.common.result.Result;
import com.baffang.user.dto.RandomPublicStationDTO;
import com.baffang.user.service.RandomPublicStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公桩随机生成Controller
 */
@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "公桩随机生成接口")
public class RandomPublicStationController {

    @Autowired
    private RandomPublicStationService randomPublicStationService;

    /**
     * 在指定经纬度范围内随机生成指定数量的公桩
     * 
     * @param longitude 中心点经度
     * @param latitude  中心点纬度
     * @param radius    半径（米）
     * @param count     生成数量
     * @return 生成的公桩列表
     */
    @PostMapping("/random-stations/radius")
    @ApiOperation("在指定经纬度范围内随机生成公桩")
    public Result<List<PublicChargeStation>> generateStationsInRadius(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radius,
            @RequestParam int count) {

        List<PublicChargeStation> stations = randomPublicStationService.generatePublicStationsInRadius(
                longitude, latitude, radius, count);

        return Result.success(stations);
    }

    /**
     * 在指定区域内随机生成指定数量的公桩
     * 
     * @param startLongitude 起始经度
     * @param startLatitude  起始纬度
     * @param endLongitude   结束经度
     * @param endLatitude    结束纬度
     * @param count          生成数量
     * @return 生成的公桩列表
     */
    @PostMapping("/random-stations/area")
    @ApiOperation("在指定区域内随机生成公桩")
    public Result<List<PublicChargeStation>> generateStationsInArea(
            @RequestParam double startLongitude,
            @RequestParam double startLatitude,
            @RequestParam double endLongitude,
            @RequestParam double endLatitude,
            @RequestParam int count) {

        List<PublicChargeStation> stations = randomPublicStationService.generatePublicStationsInArea(
                startLongitude, startLatitude, endLongitude, endLatitude, count);

        return Result.success(stations);
    }

    /**
     * 根据指定字段生成一个公桩（未指定字段随机生成）
     * 
     * @param dto 指定的公桩字段
     * @return 生成的公桩
     */
    @PostMapping("/random-stations/single")
    @ApiOperation("根据指定字段生成单个公桩")
    public Result<PublicChargeStation> generateSingleStation(
            @RequestBody RandomPublicStationDTO dto) {

        PublicChargeStation station = randomPublicStationService.generateSinglePublicStation(dto);

        return Result.success(station);
    }
}