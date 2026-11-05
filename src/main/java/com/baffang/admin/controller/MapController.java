package com.baffang.admin.controller;

import com.baffang.admin.service.AmapService;
import com.baffang.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/map")
@Tag(name = "地图服务")
@RequiredArgsConstructor
public class MapController {

    private final AmapService amapService;

    // 安全整改：补上管理员身份校验（此前仅"已登录"即可调用）
    private final com.baffang.framework.permission.AdminGuard adminGuard;

    @GetMapping("/distance")
    @Operation(summary = "计算两点之间的驾车导航距离")
    public Result<Map<String, Object>> calculateDistance(
            @Parameter(description = "起点经度") @RequestParam Double originLng,
            @Parameter(description = "起点纬度") @RequestParam Double originLat,
            @Parameter(description = "终点经度") @RequestParam Double destLng,
            @Parameter(description = "终点纬度") @RequestParam Double destLat) {

        adminGuard.requireAdmin(com.baffang.framework.permission.AdminGuard.ANY_ADMIN);
        Long distanceMeters = amapService.getDrivingDistance(originLng, originLat, destLng, destLat);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originLng", originLng);
        result.put("originLat", originLat);
        result.put("destLng", destLng);
        result.put("destLat", destLat);
        
        if (distanceMeters != null) {
            result.put("distanceMeters", distanceMeters);
            result.put("distanceKm", new BigDecimal(distanceMeters)
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP).doubleValue());
        }
        
        return Result.success(result);
    }
}
