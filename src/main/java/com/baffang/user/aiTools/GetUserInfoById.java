package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.user.service.UserService;
import com.baffang.user.vo.ProfileVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 根据用户ID获取用户信息的工具类
 */
public class GetUserInfoById extends AbstractToolWithNotification {

    private final UserService userService;

    public GetUserInfoById(UserService userService, Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.userService = userService;
    }

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息对象
     */
    @Tool(description = "根据用户ID获取用户信息，返回包含用户基本信息、认证状态等的对象。枚举字段说明：- type: 用户类型，0：普通用户，1：vip 用户；- authChargingStation: 认证充电桩状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败；- authVehicle: 认证车辆状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败")
    public ProfileVO getUserInfoById(@ToolParam(description = "用户ID") Integer userId) {
        // 发送工具调用通知
        sendToolCallNotification("getUserInfoById", "根据用户ID获取用户信息");
        
        return userService.getProfile(userId);
    }
}
