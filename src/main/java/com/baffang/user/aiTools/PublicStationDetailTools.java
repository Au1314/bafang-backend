package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.entity.PublicChargeStation;
import com.baffang.user.service.PublicStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 根据ID获取公桩信息工具
 */
public class PublicStationDetailTools extends AbstractToolWithNotification {

    private final PublicStationService publicStationService;

    public PublicStationDetailTools(PublicStationService publicStationService, Integer userId,
            WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.publicStationService = publicStationService;
    }

    /**
     * 根据公桩ID获取公桩详细信息
     * 
     * @param id 公桩ID（必须）
     * @return 公桩详细信息
     * 
     *         枚举字段说明：
     *         - currentType: 电流类型（0：直流电，1：交流电）
     *         - openStatus: 开放状态（0：关闭，1：开启）
     */
    @Tool(description = "根据公桩ID获取公桩详细信息。返回结果中包含以下枚举字段：currentType（0：直流电，1：交流电）、openStatus（0：关闭，1：开启）。")
    public PublicChargeStation getPublicStationById(
            @ToolParam(description = "公桩ID（必须）") Integer id) {
        // 发送工具调用通知
        sendToolCallNotification("getPublicStationById", "根据公桩ID获取公桩详细信息");

        return publicStationService.getPublicStationById(id);
    }
}