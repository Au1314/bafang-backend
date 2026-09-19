package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * AI对话消息表实体类
 */
@Data
public class AIChatMessage {
    /**
     * 主键（自增）
     */
    private Integer id;

    /**
     * 会话 ID（关联 ai_conversation 表）
     */
    private Integer conversationId;

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
     * AI 响应时间（毫秒，仅 AI 消息时填充）
     */
    private Integer responseTime;

    /**
     * 扩展信息（如图片 URL、语音时长等）
     */
    private String extraInfo;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 逻辑删除标识，0：正常，1：已删除
     */
    @TableLogic
    private Integer isDeleted;
}
