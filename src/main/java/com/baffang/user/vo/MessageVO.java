package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 对话消息VO
 */
@Data
public class MessageVO {
    /**
     * 消息ID
     */
    private Integer messageId;

    /**
     * 发送者ID
     */
    private Integer senderId;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 谁发送的（0-当前用户，1-对方）
     */
    private Integer whoSend;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 是否已读（0-未读，1-已读）
     */
    private Integer isRead;

    /**
     * 消息类型（0-文本，1-图片）
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 额外信息（如图片URL）
     */
    private String extraInfo;
}