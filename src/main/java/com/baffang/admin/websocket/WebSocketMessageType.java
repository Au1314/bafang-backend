package com.baffang.admin.websocket;

/**
 * WebSocket消息类型枚举
 */
public enum WebSocketMessageType {
    
    // ===== 服务端推送给客户端 =====
    
    /** 新消息 */
    NEW_MESSAGE,
    
    /** 新对话（待接入） */
    NEW_CONVERSATION,
    
    /** 对话已被接入 */
    CONVERSATION_ACCEPTED,
    
    /** 对话已结束 */
    CONVERSATION_ENDED,
    
    /** 对话已转接 */
    CONVERSATION_TRANSFERRED,
    
    /** 客服上线通知 */
    ADMIN_ONLINE,
    
    /** 客服下线通知 */
    ADMIN_OFFLINE,
    
    /** 连接成功 */
    CONNECTED,
    
    /** 心跳响应 */
    PONG,
    
    /** 错误消息 */
    ERROR,
    
    // ===== 客户端发送给服务端 =====
    
    /** 发送消息 */
    SEND_MESSAGE,
    
    /** 心跳 */
    PING,
    
    /** 已读回执 */
    READ_RECEIPT
}
