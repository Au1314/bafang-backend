package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * AI对话主表实体类
 */
@Data
public class AIChatConversation {
    /**
     * 主键（自增）
     */
    private Integer id;
    
    /**
     * 用户ID（关联user_info表）
     */
    private Integer userId;
    
    /**
     * 使用的AI模型名称
     */
    private String aiModel;
    
    /**
     * 会话ID（用于上下文管理）
     */
    private String sessionId;
    
    /**
     * 会话上下文信息（JSON格式）
     */
    private String sessionContext;
    
    /**
     * 会话状态：0-进行中，1-已结束，2-已中断
     */
    private Integer status;
    
    /**
     * 会话开始时间
     */
    private Date startTime;
    
    /**
     * 会话结束时间
     */
    private Date endTime;
    
    /**
     * 最后交互时间
     */
    private Date lastInteractionTime;
    
    /**
     * 消息总数
     */
    private Integer totalMessages;
    
    /**
     * 逻辑删除标识，0：正常，1：已删除
     */ 
    @TableLogic
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
