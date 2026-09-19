package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.service.UserService;
import com.baffang.user.vo.ProfileVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;

/**
 * WebSocket版本的用户信息工具类
 * 用于在WebSocket环境中获取用户信息
 */
public class UserInfoWSTools extends AbstractToolWithNotification {

    private final UserService userService;

    public UserInfoWSTools(UserService userService, Integer userId, WebSocketSessionManager webSocketSessionManager,
            ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.userService = userService;
    }

    @Tool(description = "获取当前登录用户的个人信息")
    public ProfileVO getCurrentUserInfo() {
        // 发送工具调用通知
        sendToolCallNotification("getCurrentUserInfo", "获取当前登录用户的个人信息");

        // 使用从构造函数传入的userId
        Integer userId = super.userId;
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 调用UserService获取用户个人信息
        return userService.getProfile(userId);
    }
}