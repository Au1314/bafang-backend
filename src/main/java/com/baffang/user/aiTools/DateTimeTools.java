package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public class DateTimeTools extends AbstractToolWithNotification {

    public DateTimeTools(Integer userId, WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
    }

    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        System.out.println("DateTimeTools.getCurrentDateTime()被调用");

        // 发送工具调用通知
        sendToolCallNotification("getCurrentDateTime", "获取当前日期时间");

        String result = LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        System.out.println("DateTimeTools.getCurrentDateTime()返回: " + result);
        return result;
    }
}