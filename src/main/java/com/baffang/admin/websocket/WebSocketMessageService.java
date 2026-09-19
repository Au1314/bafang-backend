package com.baffang.admin.websocket;

import com.baffang.admin.mapper.ConversationMapper;
import com.baffang.admin.mapper.ConversationMessageMapper;
import com.baffang.admin.mapper.ConversationParticipantMapper;
import com.baffang.common.entity.Conversation;
import com.baffang.common.entity.ConversationMessage;
import com.baffang.common.entity.ConversationParticipant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * WebSocket消息推送服务
 */
@Slf4j
@Service
public class WebSocketMessageService {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMessageMapper messageMapper;

    @Autowired
    private ConversationParticipantMapper participantMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 推送新消息给对话参与者
     */
    public void pushNewMessage(Integer conversationId, ConversationMessage message) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("messageId", message.getId());
            payload.put("content", message.getContent());
            payload.put("messageType", message.getMessageType());
            payload.put("senderId", message.getSenderId());
            payload.put("senderType", message.getSenderType());
            payload.put("sendTime", message.getSendTime() != null ? message.getSendTime().getTime() : null);
            payload.put("extraInfo", message.getExtraInfo());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.NEW_MESSAGE, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            // 从数据库获取对话中的所有管理员参与者
            List<ConversationParticipant> adminParticipants = participantMapper.selectAdminsByConversationId(conversationId);
            Set<Integer> adminIds = adminParticipants.stream()
                    .map(ConversationParticipant::getParticipantId)
                    .collect(Collectors.toSet());

            // 发送给在线的管理员
            sessionManager.sendToAdmins(adminIds, jsonMessage);

            // 同时发送给通过 joinConversation 加入的管理员
            Set<Integer> listeningAdmins = sessionManager.getConversationAdmins(Long.valueOf(conversationId));
            sessionManager.sendToAdmins(listeningAdmins, jsonMessage);

            log.debug("推送新消息: conversationId={}", conversationId);

        } catch (Exception e) {
            log.error("推送新消息失败", e);
        }
    }

    /**
     * 推送新对话通知（给所有在线客服）
     */
    public void pushNewConversation(Conversation conversation) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversation.getId());
            payload.put("type", conversation.getType());
            payload.put("status", conversation.getStatus());
            payload.put("remark", conversation.getRemark());
            payload.put("createTime", conversation.getCreateTime() != null ? conversation.getCreateTime().getTime() : null);

            // 获取对话参与者信息
            List<ConversationParticipant> participants = participantMapper.selectByConversationId(conversation.getId());
            List<Map<String, Object>> participantList = participants.stream().map(p -> {
                Map<String, Object> pMap = new HashMap<>();
                pMap.put("participantId", p.getParticipantId());
                pMap.put("participantType", p.getParticipantType());
                pMap.put("role", p.getRole());
                return pMap;
            }).collect(Collectors.toList());
            payload.put("participants", participantList);

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.NEW_CONVERSATION, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            sessionManager.broadcastToAllAdmins(jsonMessage);

            log.info("推送新对话通知: conversationId={}", conversation.getId());

        } catch (Exception e) {
            log.error("推送新对话通知失败", e);
        }
    }

    /**
     * 推送对话被接入通知
     */
    public void pushConversationAccepted(Integer conversationId, Integer adminId, String adminName) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("adminId", adminId);
            payload.put("adminName", adminName);
            payload.put("acceptTime", System.currentTimeMillis());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.CONVERSATION_ACCEPTED, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            sessionManager.broadcastToAllAdmins(jsonMessage);

            log.info("推送对话接入通知: conversationId={}, adminId={}", conversationId, adminId);

        } catch (Exception e) {
            log.error("推送对话接入通知失败", e);
        }
    }

    /**
     * 推送对话结束通知
     */
    public void pushConversationEnded(Integer conversationId, Integer adminId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("adminId", adminId);
            payload.put("endTime", System.currentTimeMillis());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.CONVERSATION_ENDED, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            List<ConversationParticipant> adminParticipants = participantMapper.selectAdminsByConversationId(conversationId);
            Set<Integer> adminIds = adminParticipants.stream()
                    .map(ConversationParticipant::getParticipantId)
                    .collect(Collectors.toSet());

            sessionManager.sendToAdmins(adminIds, jsonMessage);

            log.info("推送对话结束通知: conversationId={}", conversationId);

        } catch (Exception e) {
            log.error("推送对话结束通知失败", e);
        }
    }

    /**
     * 推送对话转接通知
     */
    public void pushConversationTransferred(Integer conversationId, Integer fromAdminId,
                                            Integer toAdminId, String toAdminName) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("fromAdminId", fromAdminId);
            payload.put("toAdminId", toAdminId);
            payload.put("toAdminName", toAdminName);
            payload.put("transferTime", System.currentTimeMillis());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.CONVERSATION_TRANSFERRED, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            sessionManager.sendToAdmin(toAdminId, jsonMessage);

            log.info("推送对话转接通知: conversationId={}, toAdminId={}", conversationId, toAdminId);

        } catch (Exception e) {
            log.error("推送对话转接通知失败", e);
        }
    }

    /**
     * 通知客服上线
     */
    public void notifyAdminOnline(Integer adminId, String adminName) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("adminId", adminId);
            payload.put("adminName", adminName);
            payload.put("onlineCount", sessionManager.getOnlineAdminCount());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.ADMIN_ONLINE, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            sessionManager.getOnlineAdminIds().stream()
                    .filter(id -> !id.equals(adminId))
                    .forEach(id -> sessionManager.sendToAdmin(id, jsonMessage));

        } catch (Exception e) {
            log.error("通知客服上线失败", e);
        }
    }

    /**
     * 通知客服下线
     */
    public void notifyAdminOffline(Integer adminId, String adminName) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("adminId", adminId);
            payload.put("adminName", adminName);
            payload.put("onlineCount", sessionManager.getOnlineAdminCount());

            WebSocketMessage wsMessage = WebSocketMessage.of(WebSocketMessageType.ADMIN_OFFLINE, payload);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            sessionManager.broadcastToAllAdmins(jsonMessage);

        } catch (Exception e) {
            log.error("通知客服下线失败", e);
        }
    }

    /**
     * 管理员通过WebSocket发送消息
     */
    public void sendMessageFromAdmin(Integer adminId, Integer conversationId, String content, Integer messageType) {
        try {
            // 保存消息到数据库
            ConversationMessage message = new ConversationMessage();
            message.setConversationId(conversationId);
            message.setSenderId(adminId);
            message.setSenderType(1); // 管理员
            message.setContent(content);
            message.setMessageType(messageType != null ? messageType : 0);
            message.setSendTime(new Date());
            message.setIsRead(0);
            message.setIsDeleted(0);

            messageMapper.insert(message);

            // 更新其他参与者的未读消息数
            participantMapper.incrementUnreadCountExceptSender(conversationId, adminId);

            // 推送消息给对话参与者
            pushNewMessage(conversationId, message);

            log.info("管理员发送消息: adminId={}, conversationId={}", adminId, conversationId);

        } catch (Exception e) {
            log.error("管理员发送消息失败", e);
        }
    }

    /**
     * 标记消息已读
     */
    public void markAsRead(Integer adminId, Integer conversationId, Integer messageId) {
        try {
            if (messageId != null) {
                messageMapper.markMessageAsReadById(messageId, adminId);
            } else {
                messageMapper.markAllMessagesAsReadByReader(conversationId, adminId);
            }
            
            // 清零未读消息数
            participantMapper.clearUnreadCount(conversationId, adminId);

            log.debug("标记已读: adminId={}, conversationId={}, messageId={}", adminId, conversationId, messageId);

        } catch (Exception e) {
            log.error("标记已读失败", e);
        }
    }
}
