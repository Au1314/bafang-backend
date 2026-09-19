package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.result.AiChatResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractToolWithNotification {

    protected final Integer userId;
    protected final WebSocketSessionManager webSocketSessionManager;
    protected final ObjectMapper objectMapper;

    public AbstractToolWithNotification(Integer userId, WebSocketSessionManager webSocketSessionManager,
            ObjectMapper objectMapper) {
        this.userId = userId;
        this.webSocketSessionManager = webSocketSessionManager;
        this.objectMapper = objectMapper;
    }

    /**
     * 发送工具调用通知
     *
     * @param toolName        工具名称
     * @param toolDescription 工具描述
     * @param data            工具调用参数
     */
    protected void sendToolCallNotification(String toolName, String toolDescription, Object data) {
        try {
            AiChatResult result = new AiChatResult();
            result.source = 2; // 来自ToolCall返回
            result.toolCallReturnType = 1; // 调用声明
            result.toolName = toolName;
            result.message = "AI正在调用工具：" + toolDescription;
            result.data = data; // 设置工具调用参数

            String jsonResult = objectMapper.writeValueAsString(result);
            webSocketSessionManager.sendToUser(userId, jsonResult);

            System.out.println("已发送工具调用通知: " + jsonResult);
        } catch (Exception e) {
            System.err.println("发送工具调用通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送工具调用通知（重载方法，无参数）
     *
     * @param toolName        工具名称
     * @param toolDescription 工具描述
     */
    protected void sendToolCallNotification(String toolName, String toolDescription) {
        sendToolCallNotification(toolName, toolDescription, null);
    }

    /**
     * 发送工具返回通知（直接返回信息）
     *
     * @param toolName 工具名称
     * @param data     要返回的数据
     */
    protected void sendToolReturnNotification(String toolName, Object data) {
        try {
            AiChatResult result = new AiChatResult();
            result.source = 2; // 来自ToolCall返回
            result.toolCallReturnType = 2; // 直接返回信息
            result.toolName = toolName;
            result.message = "";
            result.data = data;

            String jsonResult = objectMapper.writeValueAsString(result);
            webSocketSessionManager.sendToUser(userId, jsonResult);

            System.out.println("已发送工具返回通知: " + jsonResult);
        } catch (Exception e) {
            System.err.println("发送工具返回通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}