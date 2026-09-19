package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * AI对话列表响应VO
 */
@Data
public class AIChatConversationListVO {
    /**
     * 对话ID
     */
    private Integer conversationId;

    /**
     * 使用的AI模型名称
     */
    private String aiModel;

    /**
     * 会话ID（用于上下文管理）
     */
    private String sessionId;

    /**
     * 会话状态：0-进行中，1-已结束，2-已中断
     */
    private Integer status;

    /**
     * 最后交互时间
     */
    private Date lastInteractionTime;

    /**
     * 最后一条消息内容
     */
    private String lastMessage;

    /**
     * 消息总数
     */
    private Integer totalMessages;
}
