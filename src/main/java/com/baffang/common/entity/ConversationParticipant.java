package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 对话参与者表实体类
 */
@Data
public class ConversationParticipant {
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 对话 ID
     */
    private Integer conversationId;
    
    /**
     * 参与者 ID（用户 ID 或管理员 ID）
     */
    private Integer participantId;
    
    /**
     * 参与者类型：0-普通用户，1-管理员，2-系统
     */
    private Integer participantType;
    
    /**
     * 参与者角色：0-发起者，1-响应者，2-观察者
     */
    private Integer role;
    
    /**
     * 加入时间
     */
    private Date joinTime;
    
    /**
     * 最后阅读时间
     */
    private Date lastReadTime;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 逻辑删除标识，0：正常，1：已删除
     */
     @TableLogic
    private Integer isDeleted;
}