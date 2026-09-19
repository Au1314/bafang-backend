package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * AI对话消息VO
 */
@Data
public class AIChatMessageVO {
    /**
     * 消息ID
     */
    private Integer messageId;

    /**
     * 发送者类型：0-用户，1-AI
     */
    private Integer senderType;

    /**
     * 消息类型：0-文本，1-图片，2-语音，3-系统通知
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * AI响应时间（毫秒，仅AI消息时填充）
     */
    private Integer responseTime;

    /**
     * 扩展信息（如图片URL、语音时长等）
     */
    private String extraInfo;
}
