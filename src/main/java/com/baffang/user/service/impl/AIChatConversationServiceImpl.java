package com.baffang.user.service.impl;

import com.baffang.common.entity.AIChatConversation;
import com.baffang.user.mapper.UserAIChatConversationMapper;
import com.baffang.user.service.AIChatConversationService;
import com.baffang.user.vo.AIChatConversationListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * AI对话主表服务实现类
 */
@Service
public class AIChatConversationServiceImpl implements AIChatConversationService {

    @Autowired
    private UserAIChatConversationMapper aiChatConversationMapper;

    @Override
    public AIChatConversation createConversation(Integer userId, String aiModel, String sessionId) {
        AIChatConversation conversation = new AIChatConversation();
        conversation.setUserId(userId);
        conversation.setAiModel(aiModel);
        conversation.setSessionId(sessionId);
        conversation.setStatus(0); // 会话状态：0-进行中
        conversation.setStartTime(new Date());
        conversation.setTotalMessages(0);
        conversation.setIsDeleted(0);
        conversation.setCreateTime(new Date());
        conversation.setUpdateTime(new Date());

        aiChatConversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public AIChatConversation getConversationBySessionId(String sessionId) {
        return aiChatConversationMapper.selectBySessionId(sessionId);
    }

    @Override
    public void updateLastInteractionTime(Integer conversationId) {
        AIChatConversation conversation = new AIChatConversation();
        conversation.setId(conversationId);
        conversation.setLastInteractionTime(new Date());
        conversation.setUpdateTime(new Date());

        aiChatConversationMapper.updateById(conversation);
    }

    @Override
    public void updateSessionContext(Integer conversationId, String sessionContext) {
        AIChatConversation conversation = new AIChatConversation();
        conversation.setId(conversationId);
        conversation.setSessionContext(sessionContext);
        conversation.setUpdateTime(new Date());

        aiChatConversationMapper.updateById(conversation);
    }

    @Override
    public void updateStatus(Integer conversationId, Integer status) {
        AIChatConversation conversation = new AIChatConversation();
        conversation.setId(conversationId);
        conversation.setStatus(status);
        conversation.setUpdateTime(new Date());

        // 如果状态为已结束或已中断，设置结束时间
        if (status == 1 || status == 2) {
            conversation.setEndTime(new Date());
        }

        aiChatConversationMapper.updateById(conversation);
    }

    @Override
    public void updateEndTime(Integer conversationId) {
        AIChatConversation conversation = new AIChatConversation();
        conversation.setId(conversationId);
        conversation.setEndTime(new Date());
        conversation.setUpdateTime(new Date());

        aiChatConversationMapper.updateById(conversation);
    }

    @Override
    public void incrementMessageCount(Integer conversationId) {
        aiChatConversationMapper.incrementMessageCount(conversationId);
    }

    @Override
    public List<AIChatConversationListVO> getConversationListByUserId(Integer userId) {
        return aiChatConversationMapper.selectConversationListByUserId(userId);
    }
}
