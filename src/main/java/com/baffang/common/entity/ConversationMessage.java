package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 对话消息表实体类
 */
@Data
public class ConversationMessage {
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 对话 ID
     */
    private Integer conversationId;
    
    /**
     * 发送者 ID
     */
    private Integer senderId;
    
    /**
     * 发送者类型：0-普通用户，1-管理员，2-系统
     */
    private Integer senderType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：0-文本，1-图片，2-语音，3-系统通知
     */
    private Integer messageType;
    
    /**
     * 扩展信息（如图片 URL、语音时长等）
     */
    private String extraInfo;
    
    /**
     * 发送时间
     */
    private Date sendTime;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 逻辑删除标识，0：正常，1：已删除
     */
    @TableLogic 
    private Integer isDeleted;
}