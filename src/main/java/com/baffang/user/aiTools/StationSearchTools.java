package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.user.dto.StationSearchResultDTO;
import com.baffang.user.service.StationSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 充电桩搜索工具类
 */
public class StationSearchTools extends AbstractToolWithNotification {

    private final StationSearchService stationSearchService;

    public StationSearchTools(StationSearchService stationSearchService, Integer userId,
            WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.stationSearchService = stationSearchService;
    }

    /**
     * 根据经纬度和范围查询周围所有充电桩
     * 
     * @param longitude   中心经度（必须）
     * @param latitude    中心纬度（必须）
     * @param radius      搜索半径（米，必须）
     * @param stationType 充电桩类型（0：只要私桩，1：只要公桩，2：两者皆有，默认2）
     * @param sortType    排序规则（1：评分优先，2：距离优先，3：价格优先，默认2）
     * @return 充电桩列表
     * 
     *         枚举字段说明：
     *         - openStatus: 开放状态（0：关闭，1：开启）
     *         - stationType: 充电桩类型（0：私桩，1：公桩）
     */
    @Tool(description = "根据经纬度和范围查询周围所有充电桩，支持筛选和排序。返回结果中包含以下枚举字段：openStatus（0：关闭，1：开启）、stationType（0：私桩，1：公桩）。")
    public List<StationSearchResultDTO> searchStations(
            @ToolParam(description = "中心经度（必须）") Double longitude,
            @ToolParam(description = "中心纬度（必须）") Double latitude,
            @ToolParam(description = "搜索半径（米，必须）") Double radius,
            @ToolParam(description = "充电桩类型（0：只要私桩，1：只要公桩，2：两者皆有，默认2）") Integer stationType,
            @ToolParam(description = "排序规则（1：评分优先，2：距离优先，3：价格优先，默认2）") Integer sortType) {
        // 发送工具调用通知
        sendToolCallNotification("searchStations", "根据经纬度和范围查询周围所有充电桩");

        // 参数验证
        if (longitude == null || latitude == null || radius == null) {
            throw new IllegalArgumentException("经度、纬度和搜索半径不能为空");
        }

        // 设置默认值
        if (stationType == null || stationType < 0 || stationType > 2) {
            stationType = 2;
        }

        if (sortType == null || sortType < 1 || sortType > 3) {
            sortType = 2;
        }

        // 调用服务查询充电桩
        return stationSearchService.searchStationsByGeoRange(longitude, latitude, radius, stationType, sortType);
    }
}