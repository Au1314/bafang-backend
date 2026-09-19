package com.baffang.admin.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    
    /** 消息类型 */
    private String type;
    
    /** 消息内容 */
    private Object payload;
    
    /** 时间戳 */
    private Long timestamp;
    
    /**
     * 创建消息
     */
    public static WebSocketMessage of(WebSocketMessageType type, Object payload) {
        return WebSocketMessage.builder()
                .type(type.name())
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建错误消息
     */
    public static WebSocketMessage error(String message) {
        return WebSocketMessage.builder()
                .type(WebSocketMessageType.ERROR.name())
                .payload(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
