package com.baffang.user.service;

import com.baffang.common.entity.AIChatConversation;
import com.baffang.user.vo.AIChatConversationListVO;

import java.util.List;

/**
 * AI对话主表服务接口
 */
public interface AIChatConversationService {
    /**
     * 创建新的AI对话会话
     *
     * @param userId    用户ID
     * @param aiModel   AI模型名称
     * @param sessionId 会话ID
     * @return 创建的对话会话
     */
    AIChatConversation createConversation(Integer userId, String aiModel, String sessionId);

    /**
     * 根据会话ID获取对话会话
     *
     * @param sessionId 会话ID
     * @return 对话会话
     */
    AIChatConversation getConversationBySessionId(String sessionId);

    /**
     * 更新对话会话的最后交互时间
     *
     * @param conversationId 对话ID
     */
    void updateLastInteractionTime(Integer conversationId);

    /**
     * 更新对话会话的上下文信息
     *
     * @param conversationId 对话ID
     * @param sessionContext 会话上下文信息
     */
    void updateSessionContext(Integer conversationId, String sessionContext);

    /**
     * 更新对话会话的状态
     *
     * @param conversationId 对话ID
     * @param status         会话状态
     */
    void updateStatus(Integer conversationId, Integer status);

    /**
     * 更新对话会话的结束时间
     *
     * @param conversationId 对话ID
     */
    void updateEndTime(Integer conversationId);

    /**
     * 增加消息计数
     *
     * @param conversationId 对话ID
     */
    void incrementMessageCount(Integer conversationId);

    /**
     * 获取用户的AI对话列表
     *
     * @param userId 用户ID
     * @return 对话列表
     */
    List<AIChatConversationListVO> getConversationListByUserId(Integer userId);
}
