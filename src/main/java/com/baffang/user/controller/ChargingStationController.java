package com.baffang.user.controller;

import com.baffang.common.result.Result;
import com.baffang.user.dto.ChargingStationRequest;
import com.baffang.user.service.ChargingStationService;
import com.baffang.user.vo.ChargingStationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "充电桩相关接口")
public class ChargingStationController {
    
    @Resource
    private ChargingStationService chargingStationService;
    
    @PostMapping("/nearby")
    @ApiOperation("根据经纬度搜索附近的充电站")
    public Result<List<ChargingStationVO>> findNearbyStations(@Valid @RequestBody ChargingStationRequest request) {
        try {
            List<ChargingStationVO> stations = chargingStationService.findNearbyStations(request);
            log.info("成功找到{}个充电站", stations != null ? stations.size() : 0);
            return Result.success(stations);
        } catch (Exception e) {
            log.error("搜索充电站失败", e);
            return Result.error("搜索充电站失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public Result<String> health() {
        return Result.success("充电桩服务正常运行");
    }
}