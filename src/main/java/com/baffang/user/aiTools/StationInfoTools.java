package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.PublicChargeStation;
import com.baffang.common.util.GeoUtils;
import com.baffang.user.service.PrivateStationService;
import com.baffang.user.service.PublicStationService;
import com.baffang.user.vo.ChargingPrivateStationInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StationInfoTools extends AbstractToolWithNotification {

    private final PrivateStationService privateStationService;
    private final PublicStationService publicStationService;

    public StationInfoTools(PrivateStationService privateStationService, PublicStationService publicStationService,
            Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationService = privateStationService;
        this.publicStationService = publicStationService;
    }

    @Tool(description = "向用户展示充电桩简略信息,该接口会直接向用户展示信息，无需你再复述", returnDirect = true)
    public void getStationInfo(@ToolParam(description = "充电桩ID") Integer stationId,
            @ToolParam(description = "充电桩类型（1-私桩，2-公桩）") Integer stationType,
            @ToolParam(description = "用户当前经度") Double longitude,
            @ToolParam(description = "用户当前纬度") Double latitude) {
        // 发送工具调用通知
        sendToolCallNotification("getStationInfo", "获取充电桩简略信息", Map.of(
                "stationId", stationId,
                "stationType", stationType,
                "longitude", longitude,
                "latitude", latitude));

        Map<String, Object> stationInfo = new HashMap<>();
        double distance = 0;

        if (stationType == 1) {
            // 私桩
            ChargingPrivateStationInfoVO privateStation = privateStationService
                    .getChargingPrivateStationInfo(stationId);
            if (privateStation != null) {
                // 计算距离
                distance = GeoUtils.calculateDistance(
                        longitude,
                        latitude,
                        privateStation.getLongitude().doubleValue(),
                        privateStation.getLatitude().doubleValue());

                // 构建私桩简略信息
                stationInfo.put("id", privateStation.getId());
                stationInfo.put("name", privateStation.getName());
                stationInfo.put("locationName", privateStation.getLocationName());
                stationInfo.put("longitude", privateStation.getLongitude());
                stationInfo.put("latitude", privateStation.getLatitude());
                stationInfo.put("price", privateStation.getPrice());
                stationInfo.put("rating", privateStation.getRating());
                stationInfo.put("distance", distance);
                stationInfo.put("type", 1); // 私桩
                stationInfo.put("openStatus", privateStation.getOpenStatus());
                stationInfo.put("maxVehicles", privateStation.getMaxVehicles());
                stationInfo.put("currentVehicles", privateStation.getCurrentVehicles());
                stationInfo.put("currentType", privateStation.getCurrentType());
                stationInfo.put("maxChargeVoltage", privateStation.getMaxChargeVoltage());
                stationInfo.put("maxChargeCurrent", privateStation.getMaxChargeCurrent());
                stationInfo.put("maxChargePower", privateStation.getMaxChargePower());
            }
        } else if (stationType == 2) {
            // 公桩
            PublicChargeStation publicStation = publicStationService.getPublicStationById(stationId);
            if (publicStation != null) {
                // 计算距离
                distance = GeoUtils.calculateDistance(
                        longitude,
                        latitude,
                        publicStation.getLongitude(),
                        publicStation.getLatitude());

                // 构建公桩简略信息
                stationInfo.put("id", publicStation.getId());
                stationInfo.put("name", publicStation.getName());
                stationInfo.put("locationName", publicStation.getLocationName());
                stationInfo.put("longitude", publicStation.getLongitude());
                stationInfo.put("latitude", publicStation.getLatitude());
                stationInfo.put("price", publicStation.getPrice());
                stationInfo.put("rating", publicStation.getRating());
                stationInfo.put("distance", distance);
                stationInfo.put("type", 2); // 公桩
                stationInfo.put("openStatus", publicStation.getOpenStatus());
                stationInfo.put("maxVehicles", publicStation.getMaxVehicles());
                stationInfo.put("currentVehicles", publicStation.getCurrentVehicles());
                stationInfo.put("currentType", publicStation.getCurrentType());
                stationInfo.put("maxChargeVoltage", publicStation.getMaxChargeVoltage());
                stationInfo.put("maxChargeCurrent", publicStation.getMaxChargeCurrent());
                stationInfo.put("maxChargePower", publicStation.getMaxChargePower());
            }
        }

        // 发送工具返回通知
        sendToolReturnNotification("getStationInfo", stationInfo);
    }
}