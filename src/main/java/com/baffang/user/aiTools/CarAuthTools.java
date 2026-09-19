package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.entity.CarAuth;
import com.baffang.user.service.CarAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 车辆认证信息工具类
 */
public class CarAuthTools extends AbstractToolWithNotification {

    private final CarAuthService carAuthService;

    public CarAuthTools(CarAuthService carAuthService, Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.carAuthService = carAuthService;
    }

    /**
     * 获取当前登录用户的认证车辆信息
     * 
     * @return 车辆认证信息对象
     * 
     *         枚举字段说明：
     *         - currentType: 电流类型，0：直流电，1：交流电
     *         - authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败
     */
    @Tool(description = "获取当前登录用户的认证车辆信息，返回包含车辆基本信息、技术参数和认证状态的对象。枚举字段说明：- currentType: 电流类型，0：直流电，1：交流电；- authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败")
    public CarAuth getCurrentUserCarAuth() {
        // 发送工具调用通知
        sendToolCallNotification("getCurrentUserCarAuth", "获取当前登录用户的认证车辆信息");
        
        // 使用从构造函数传入的userId
        Integer userId = super.userId;
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 调用CarAuthService获取车辆认证信息
        return carAuthService.getCarAuthByUserId(userId);
    }

}
