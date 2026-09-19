package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.user.service.PrivateStationService;
import com.baffang.user.vo.ChargingPrivateStationInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 私桩详细信息工具类
 */
public class PrivateStationDetailTools extends AbstractToolWithNotification {

    private final PrivateStationService privateStationService;

    public PrivateStationDetailTools(PrivateStationService privateStationService, Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationService = privateStationService;
    }

    /**
     * 根据私桩ID获取私桩详细数据
     * 
     * @param stationId 私桩ID
     * @return 私桩详细信息，包含基本信息和技术参数
     * 
     *         枚举字段说明：
     *         - currentType: 电流类型，0：直流电，1：交流电
     *         - type: 私桩类型，0：普通私桩，1：专业私桩
     *         - status: 私桩状态，0：空闲，1：已预约，2：已充电，3：异常
     *         - openStatus: 开放状态，0：关闭，1：开启
     *         - authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败
     *         - priceType: 价格类型，0-静态价格，1-动态价格
     */
    @Tool(description = "根据私桩ID获取私桩详细数据，返回包含基本信息和技术参数的对象。枚举字段说明：- currentType: 电流类型，0：直流电，1：交流电；- type: 私桩类型，0：普通私桩，1：专业私桩；- status: 私桩状态，0：空闲，1：已预约，2：已充电，3：异常；- openStatus: 开放状态，0：关闭，1：开启；- authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败；- priceType: 价格类型，0-静态价格，1-动态价格")
    public ChargingPrivateStationInfoVO getPrivateStationDetail(@ToolParam(description = "私桩ID") Integer stationId) {
        // 发送工具调用通知
        sendToolCallNotification("getPrivateStationDetail", "根据私桩ID获取私桩详细数据");
        
        // 调用PrivateStationService获取私桩详细信息
        return privateStationService.getChargingPrivateStationInfo(stationId);
    }
}