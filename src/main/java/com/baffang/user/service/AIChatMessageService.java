package com.baffang.user.service;

import java.util.List;

import com.baffang.common.entity.AIChatMessage;
import com.baffang.user.vo.AIChatMessageVO;

/**
 * AI对话消息表服务接口
 */
public interface AIChatMessageService {
    /**
     * 保存用户发送的消息
     *
     * @param conversationId 对话ID
     * @param content        消息内容
     * @return 保存的消息
     */
    AIChatMessage saveUserMessage(Integer conversationId, String content);

    /**
     * 保存AI发送的消息
     *
     * @param conversationId 对话ID
     * @param content        消息内容
     * @param responseTime   响应时间（毫秒）
     * @return 保存的消息
     */
    AIChatMessage saveAIMessage(Integer conversationId, String content, Integer responseTime);

    /**
     * 保存系统发送的消息
     *
     * @param conversationId 对话ID
     * @param content        消息内容
     * @return 保存的消息
     */
    AIChatMessage saveSystemMessage(Integer conversationId, String content);

    /**
     * 保存工具调用消息
     *
     * @param conversationId 对话ID
     * @param content        工具调用内容
     * @return 保存的消息
     */
    AIChatMessage saveToolCallMessage(Integer conversationId, String content);

    /**
     * 查询指定对话的消息历史
     *
     * @param conversationId 对话ID
     * @return 消息历史列表
     */
    List<AIChatMessage> getMessageHistoryByConversationId(Integer conversationId);

    /**
     * 查询指定对话的消息历史并转换为VO
     *
     * @param conversationId 对话ID
     * @return 消息历史VO列表
     */
    List<AIChatMessageVO> getMessageHistoryVOByConversationId(Integer conversationId);
}
