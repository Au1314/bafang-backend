package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.dto.PrivateStationReserveDTO;
import com.baffang.user.service.ConversationService;
import com.baffang.user.service.PrivateStationUseService;
import com.baffang.user.vo.ConversationCreateVO;
import com.baffang.user.vo.PrivateStationReserveVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 私桩预约工具类
 */
public class PrivateStationReserveTools extends AbstractToolWithNotification {

    private final PrivateStationUseService privateStationUseService;
    private final ConversationService conversationService;

    public PrivateStationReserveTools(PrivateStationUseService privateStationUseService,
            ConversationService conversationService, Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationUseService = privateStationUseService;
        this.conversationService = conversationService;
    }

    /**
     * 预约指定私桩
     * 
     * @param stationId   私桩ID（必须）
     * @param reserveTime 预约时间（格式：yyyy-MM-dd HH:mm:ss，例如：2026-01-15 14:30:00，必须）
     * @return 预约结果
     * 
     *         枚举字段说明：
     *         - status: 预约状态（0：待确认）
     */
    @Tool(description = "预约指定私桩。需要提供私桩ID和预约时间，预约时间格式为：yyyy-MM-dd HH:mm:ss，例如：2026-01-15 14:30:00。返回结果中包含以下枚举字段：status（0：待确认）。")
    public PrivateStationReserveVO reservePrivateStation(
            @ToolParam(description = "私桩ID（必须）") Integer stationId,
            @ToolParam(description = "预约时间（格式：yyyy-MM-dd HH:mm:ss，例如：2026-01-15 14:30:00，必须）") String reserveTime) {
        // 发送工具调用通知
        sendToolCallNotification("reservePrivateStation", "预约指定私桩");

        // 1. 参数验证
        Integer userId = super.userId; // 使用从构造函数传入的userId
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 5. 参数验证
        if (stationId == null) {
            throw new BusinessException("私桩ID不能为空");
        }
        if (reserveTime == null || reserveTime.isEmpty()) {
            throw new BusinessException("预约时间不能为空");
        }

        // 6. 构造预约请求DTO
        PrivateStationReserveDTO reserveDTO = new PrivateStationReserveDTO();
        reserveDTO.setReserveTime(reserveTime);

        // 7. 调用服务层方法进行预约
        PrivateStationReserveVO reserveResult = privateStationUseService.reservePrivateStation(userId, stationId,
                reserveDTO);

        // 8. 预约成功后创建与私桩桩主的对话
        ConversationCreateVO conversationResult = conversationService.createConversationWithStationOwner(userId,
                stationId);

        // 9. 将对话ID添加到返回结果中（注意：这里需要修改PrivateStationReserveVO类来支持conversationId字段，
        // 但为了保持现有接口的兼容性，我们直接返回结果说明）

        return reserveResult;
    }
}