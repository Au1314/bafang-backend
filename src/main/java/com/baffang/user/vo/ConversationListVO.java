package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 对话列表响应VO
 */
@Data
public class ConversationListVO {
    /**
     * 对话ID
     */
    private Integer conversationId;

    /**
     * 对话类型：1-预约
     */
    private Integer type;

    /**
     * 对话状态：1-进行中，2-已完成，3-已关闭
     */
    private Integer status;

    /**
     * 最后消息时间
     */
    private Date lastMessageTime;

    /**
     * 最后消息内容
     */
    private String lastMessage;

    /**
     * 最后消息发送者
     */
    private String lastMessageSender;

    /**
     * 目标名称（桩主名称或用户名称）
     */
    private String targetName;
}