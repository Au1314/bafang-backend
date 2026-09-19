package com.baffang.user.service.impl;

import com.baffang.common.entity.AIChatMessage;
import com.baffang.common.enums.entityEnum.AIMessageSenderTypeEnum;
import com.baffang.common.enums.entityEnum.ConversationMessageTypeEnum;
import com.baffang.user.mapper.UserAIChatMessageMapper;
import com.baffang.user.service.AIChatMessageService;
import com.baffang.user.vo.AIChatMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * AI对话消息表服务实现类
 */
@Service
public class AIChatMessageServiceImpl implements AIChatMessageService {

    @Autowired
    private UserAIChatMessageMapper aiChatMessageMapper;

    @Override
    public AIChatMessage saveUserMessage(Integer conversationId, String content) {
        AIChatMessage message = new AIChatMessage();
        message.setConversationId(conversationId);
        message.setSenderType(AIMessageSenderTypeEnum.USER.getCode());
        message.setMessageType(ConversationMessageTypeEnum.TEXT.getCode());
        message.setContent(content);
        message.setSendTime(new Date());
        message.setIsDeleted(0);

        aiChatMessageMapper.insert(message);
        return message;
    }

    @Override
    public AIChatMessage saveAIMessage(Integer conversationId, String content, Integer responseTime) {
        AIChatMessage message = new AIChatMessage();
        message.setConversationId(conversationId);
        message.setSenderType(AIMessageSenderTypeEnum.AI.getCode());
        message.setMessageType(ConversationMessageTypeEnum.TEXT.getCode());
        message.setContent(content);
        message.setResponseTime(responseTime);
        message.setSendTime(new Date());
        message.setIsDeleted(0);

        aiChatMessageMapper.insert(message);
        return message;
    }

    @Override
    public AIChatMessage saveSystemMessage(Integer conversationId, String content) {
        AIChatMessage message = new AIChatMessage();
        message.setConversationId(conversationId);
        message.setSenderType(AIMessageSenderTypeEnum.AI.getCode()); // 系统消息也视为AI发送
        message.setMessageType(ConversationMessageTypeEnum.SYSTEM_NOTIFICATION.getCode());
        message.setContent(content);
        message.setSendTime(new Date());
        message.setIsDeleted(0);

        aiChatMessageMapper.insert(message);
        return message;
    }

    @Override
    public AIChatMessage saveToolCallMessage(Integer conversationId, String content) {
        AIChatMessage message = new AIChatMessage();
        message.setConversationId(conversationId);
        message.setSenderType(AIMessageSenderTypeEnum.AI.getCode()); // 工具调用视为AI发送
        message.setMessageType(ConversationMessageTypeEnum.TEXT.getCode());
        message.setContent(content);
        message.setSendTime(new Date());
        message.setIsDeleted(0);

        aiChatMessageMapper.insert(message);
        return message;
    }

    @Override
    public List<AIChatMessage> getMessageHistoryByConversationId(Integer conversationId) {
        return aiChatMessageMapper.selectMessageHistoryByConversationId(conversationId);
    }

    @Override
    public List<AIChatMessageVO> getMessageHistoryVOByConversationId(Integer conversationId) {
        List<AIChatMessage> messages = aiChatMessageMapper.selectMessageHistoryByConversationId(conversationId);

        // 转换为VO列表
        return messages.stream().map(message -> {
            AIChatMessageVO vo = new AIChatMessageVO();
            vo.setMessageId(message.getId());
            vo.setSenderType(message.getSenderType());
            vo.setMessageType(message.getMessageType());
            vo.setContent(message.getContent());
            vo.setSendTime(message.getSendTime());
            vo.setResponseTime(message.getResponseTime());
            vo.setExtraInfo(message.getExtraInfo());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }
}
