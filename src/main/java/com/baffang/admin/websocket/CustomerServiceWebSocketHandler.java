package com.baffang.admin.websocket;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.admin.mapper.ConversationMapper;
import com.baffang.admin.mapper.ConversationParticipantMapper;
import com.baffang.admin.mapper.ConversationMessageMapper;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.Conversation;
import com.baffang.common.entity.ConversationParticipant;
import com.baffang.common.entity.ConversationMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

/**
 * 客服WebSocket处理器
 */
@Slf4j
@Component
public class CustomerServiceWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private WebSocketMessageService messageService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationParticipantMapper participantMapper;

    @Autowired
    private ConversationMessageMapper messageMapper;

    @Autowired
    private ObjectMapper objectMapper;  // ✅ 注入全局配置的 ObjectMapper


    /**
     * 连接建立
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer adminId = (Integer) session.getAttributes().get("adminId");
        
        if (adminId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        
        sessionManager.addAdminSession(adminId, session);
        
        AdminInfo admin = adminMapper.selectById(Long.valueOf(adminId));
        String adminName = admin != null ? admin.getName() : "未知";
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("adminId", adminId);
        payload.put("adminName", adminName);
        payload.put("onlineCount", sessionManager.getOnlineAdminCount());
        
        WebSocketMessage connectedMsg = WebSocketMessage.of(WebSocketMessageType.CONNECTED, payload);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connectedMsg)));
        
        messageService.notifyAdminOnline(adminId, adminName);
        
        log.info("WebSocket连接建立: adminId={}, sessionId={}", adminId, session.getId());
    }

    /**
     * 收到消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer adminId = (Integer) session.getAttributes().get("adminId");
        String payload = message.getPayload();
        
        log.info("收到WebSocket消息: adminId={}, message={}", adminId, payload);
        
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "";
            
            switch (type) {
                case "PING":
                    WebSocketMessage pongMsg = WebSocketMessage.of(WebSocketMessageType.PONG, "pong");
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMsg)));
                    break;
                    
                case "SEND_MESSAGE":
                    handleSendMessage(adminId, jsonNode);
                    break;
                    
                case "READ_RECEIPT":
                    handleReadReceipt(adminId, jsonNode);
                    break;
                    
                case "JOIN_CONVERSATION":
                    if (jsonNode.has("conversationId")) {
                        Long conversationId = jsonNode.get("conversationId").asLong();
                        sessionManager.joinConversation(conversationId, adminId);
                        log.info("管理员加入对话: adminId={}, conversationId={}", adminId, conversationId);
                    }
                    break;
                    
                case "LEAVE_CONVERSATION":
                    if (jsonNode.has("conversationId")) {
                        Long leaveConvId = jsonNode.get("conversationId").asLong();
                        sessionManager.leaveConversation(leaveConvId, adminId);
                        log.info("管理员离开对话: adminId={}, conversationId={}", adminId, leaveConvId);
                    }
                    break;
                
                case "GET_CONVERSATIONS":
                    handleGetConversations(session, adminId);
                    break;
                    
                case "GET_CONVERSATION_MESSAGES":
                    handleGetConversationMessages(session, jsonNode);
                    break;
                    
                case "ACCEPT_CONVERSATION":
                    handleAcceptConversation(session, adminId, jsonNode);
                    break;
                    
                case "CLOSE_CONVERSATION":
                    handleCloseConversation(session, adminId, jsonNode);
                    break;
                    
                case "TRANSFER_CONVERSATION":
                    handleTransferConversation(session, adminId, jsonNode);
                    break;
                    
                case "GET_ONLINE_ADMINS":
                    handleGetOnlineAdmins(session);
                    break;
                    
                default:
                    log.warn("未知的消息类型: {}", type);
                    WebSocketMessage unknownMsg = WebSocketMessage.error("未知的消息类型: " + type);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unknownMsg)));
            }
            
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            WebSocketMessage errorMsg = WebSocketMessage.error("消息处理失败: " + e.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMsg)));
        }
    }

    private void handleSendMessage(Integer adminId, JsonNode jsonNode) {
        if (!jsonNode.has("conversationId") || !jsonNode.has("content")) {
            log.warn("发送消息缺少必要参数");
            return;
        }
        
        Integer conversationId = jsonNode.get("conversationId").asInt();
        String content = jsonNode.get("content").asText();
        Integer messageType = jsonNode.has("messageType") ? jsonNode.get("messageType").asInt() : 0;
        
        messageService.sendMessageFromAdmin(adminId, conversationId, content, messageType);
    }

    private void handleReadReceipt(Integer adminId, JsonNode jsonNode) {
        if (!jsonNode.has("conversationId")) {
            log.warn("已读回执缺少conversationId");
            return;
        }
        
        Integer conversationId = jsonNode.get("conversationId").asInt();
        Integer messageId = jsonNode.has("messageId") ? jsonNode.get("messageId").asInt() : null;
        
        messageService.markAsRead(adminId, conversationId, messageId);
    }

    private void handleGetConversations(WebSocketSession session, Integer adminId) {
        try {
            // 获取待接入的会话
            PageHelper.startPage(1, 100);
            Page<Map<String, Object>> waitingPage = conversationMapper.selectPendingConversations();
            List<Map<String, Object>> waitingList = new ArrayList<>(waitingPage.getResult());
            
            // 获取我正在处理的会话
            List<?> myList = conversationMapper.selectMyConversations(adminId, 1);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("waiting", waitingList);
            payload.put("processing", myList);
            payload.put("waitingCount", waitingList.size());
            payload.put("processingCount", myList != null ? myList.size() : 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CONVERSATIONS");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            log.info("发送会话列表给管理员: adminId={}, waiting={}, processing={}", 
                    adminId, waitingList.size(), myList != null ? myList.size() : 0);
            
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            sendError(session, "获取会话列表失败: " + e.getMessage());
        }
    }

    private void handleGetConversationMessages(WebSocketSession session, JsonNode jsonNode) {
        try {
            if (!jsonNode.has("conversationId")) {
                sendError(session, "缺少 conversationId");
                return;
            }
            
            Integer conversationId = jsonNode.get("conversationId").asInt();
            Integer page = jsonNode.has("page") ? jsonNode.get("page").asInt() : 1;
            Integer size = jsonNode.has("size") ? jsonNode.get("size").asInt() : 50;
            
            Conversation conversation = conversationMapper.selectById(conversationId);
            
            PageHelper.startPage(page, size);
            Page<Map<String, Object>> msgPage = messageMapper.selectMessagesByConversationId(conversationId);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("conversation", conversation);
            payload.put("messages", msgPage.getResult());
            payload.put("page", page);
            payload.put("size", size);
            payload.put("total", msgPage.getTotal());
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CONVERSATION_MESSAGES");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            log.info("发送消息历史: conversationId={}, messageCount={}", conversationId, msgPage.size());
            
        } catch (Exception e) {
            log.error("获取消息历史失败", e);
            sendError(session, "获取消息历史失败: " + e.getMessage());
        }
    }

    private void handleAcceptConversation(WebSocketSession session, Integer adminId, JsonNode jsonNode) {
        try {
            if (!jsonNode.has("conversationId")) {
                sendError(session, "缺少 conversationId");
                return;
            }
            
            Integer conversationId = jsonNode.get("conversationId").asInt();
            
            // 检查是否已有管理员接入
            ConversationParticipant existAdmin = participantMapper.selectAdminParticipant(conversationId);
            if (existAdmin != null) {
                sendError(session, "该会话已被其他客服接入");
                return;
            }
            
            // 添加管理员为参与者（使用 Date 而不是 LocalDateTime）
            ConversationParticipant participant = new ConversationParticipant();
            participant.setConversationId(conversationId);
            participant.setParticipantId(adminId);
            participant.setParticipantType(1);
            participant.setRole(1);
            participant.setJoinTime(new Date());  // 使用 Date
            participant.setUnreadCount(0);
            participant.setIsDeleted(0);
            
            participantMapper.insert(participant);
            
            sessionManager.joinConversation(conversationId.longValue(), adminId);
            
            Map<String, Object> convDetail = conversationMapper.selectConversationDetail(conversationId);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("adminId", adminId);
            payload.put("conversation", convDetail);
            payload.put("success", true);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CONVERSATION_ACCEPTED");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            log.info("管理员接入会话成功: adminId={}, conversationId={}", adminId, conversationId);
            
            // 获取管理员名称并通知
            AdminInfo admin = adminMapper.selectById(Long.valueOf(adminId));
            String adminName = admin != null ? admin.getName() : "客服";
            messageService.pushConversationAccepted(conversationId, adminId, adminName);
            
        } catch (Exception e) {
            log.error("接入会话失败", e);
            sendError(session, "接入会话失败: " + e.getMessage());
        }
    }

    private void handleCloseConversation(WebSocketSession session, Integer adminId, JsonNode jsonNode) {
        try {
            if (!jsonNode.has("conversationId")) {
                sendError(session, "缺少 conversationId");
                return;
            }
            
            Integer conversationId = jsonNode.get("conversationId").asInt();
            String closeReason = jsonNode.has("reason") ? jsonNode.get("reason").asText() : "客服主动关闭";
            
            // 更新会话状态（使用 Date 而不是 LocalDateTime）
            Conversation conversation = new Conversation();
            conversation.setId(conversationId);
            conversation.setStatus(3);
            conversation.setEndTime(new Date());  // 使用 Date
            conversation.setRemark(closeReason);
            conversation.setUpdateTime(new Date());  // 使用 Date
            
            conversationMapper.updateById(conversation);
            
            sessionManager.leaveConversation(conversationId.longValue(), adminId);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("success", true);
            payload.put("reason", closeReason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CONVERSATION_CLOSED");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            log.info("会话已关闭: conversationId={}, adminId={}", conversationId, adminId);
            
            // 通知相关人员
            messageService.pushConversationEnded(conversationId, adminId);
            
        } catch (Exception e) {
            log.error("关闭会话失败", e);
            sendError(session, "关闭会话失败: " + e.getMessage());
        }
    }

    private void handleTransferConversation(WebSocketSession session, Integer adminId, JsonNode jsonNode) {
        try {
            if (!jsonNode.has("conversationId") || !jsonNode.has("targetAdminId")) {
                sendError(session, "缺少 conversationId 或 targetAdminId");
                return;
            }
            
            Integer conversationId = jsonNode.get("conversationId").asInt();
            Integer targetAdminId = jsonNode.get("targetAdminId").asInt();
            
            // 软删除当前管理员的参与记录
            ConversationParticipant currentParticipant = participantMapper.selectByConversationAndParticipant(
                    conversationId, adminId, 1);
            if (currentParticipant != null) {
                currentParticipant.setIsDeleted(1);
                participantMapper.updateById(currentParticipant);
            }
            
            // 添加新管理员为参与者
            ConversationParticipant newParticipant = new ConversationParticipant();
            newParticipant.setConversationId(conversationId);
            newParticipant.setParticipantId(targetAdminId);
            newParticipant.setParticipantType(1);
            newParticipant.setRole(1);
            newParticipant.setJoinTime(new Date());  // 使用 Date
            newParticipant.setUnreadCount(0);
            newParticipant.setIsDeleted(0);
            
            participantMapper.insert(newParticipant);
            
            sessionManager.leaveConversation(conversationId.longValue(), adminId);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", conversationId);
            payload.put("fromAdminId", adminId);
            payload.put("toAdminId", targetAdminId);
            payload.put("success", true);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "CONVERSATION_TRANSFERRED");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            
            // 通知目标管理员
            AdminInfo targetAdmin = adminMapper.selectById(Long.valueOf(targetAdminId));
            String targetAdminName = targetAdmin != null ? targetAdmin.getName() : "客服";
            messageService.pushConversationTransferred(conversationId, adminId, targetAdminId, targetAdminName);
            
            log.info("会话转接成功: conversationId={}, from={}, to={}", conversationId, adminId, targetAdminId);
            
        } catch (Exception e) {
            log.error("转接会话失败", e);
            sendError(session, "转接会话失败: " + e.getMessage());
        }
    }

    private void handleGetOnlineAdmins(WebSocketSession session) {
        try {
            // 返回的是 Set<Integer>，直接使用
            Set<Integer> onlineAdminIds = sessionManager.getOnlineAdminIds();
            
            List<Map<String, Object>> adminList = new ArrayList<>();
            for (Integer id : onlineAdminIds) {
                AdminInfo admin = adminMapper.selectById(Long.valueOf(id));
                if (admin != null) {
                    Map<String, Object> adminInfo = new HashMap<>();
                    adminInfo.put("adminId", id);
                    adminInfo.put("name", admin.getName());
                    adminInfo.put("avatar", admin.getAvatar());
                    adminList.add(adminInfo);
                }
            }
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("admins", adminList);
            payload.put("count", adminList.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "ONLINE_ADMINS");
            response.put("payload", payload);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            
        } catch (Exception e) {
            log.error("获取在线客服列表失败", e);
            sendError(session, "获取在线客服列表失败: " + e.getMessage());
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            WebSocketMessage errorMsg = WebSocketMessage.error(errorMessage);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMsg)));
        } catch (Exception e) {
            log.error("发送错误消息失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer adminId = (Integer) session.getAttributes().get("adminId");
        
        if (adminId != null) {
            sessionManager.removeAdminSession(adminId);
            
            AdminInfo admin = adminMapper.selectById(Long.valueOf(adminId));
            String adminName = admin != null ? admin.getName() : "未知";
            
            messageService.notifyAdminOffline(adminId, adminName);
        }
        
        log.info("WebSocket连接关闭: adminId={}, status={}", adminId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Integer adminId = (Integer) session.getAttributes().get("adminId");
        log.error("WebSocket传输错误: adminId={}", adminId, exception);
        
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}
