package com.baffang.admin.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket会话管理器
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    /** 管理员ID -> Session */
    private final Map<Integer, WebSocketSession> adminSessions = new ConcurrentHashMap<>();

    /** 用户ID -> Session */
    private final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    /** 用户ID -> 当前选择的会话ID */
    private final Map<Integer, String> userCurrentSessionIds = new ConcurrentHashMap<>();

    /** 对话ID -> 参与者adminId列表 */
    private final Map<Long, Set<Integer>> conversationAdmins = new ConcurrentHashMap<>();

    /**
     * 添加管理员会话
     */
    public void addAdminSession(Integer adminId, WebSocketSession session) {
        // 如果已有连接，先关闭旧连接
        WebSocketSession oldSession = adminSessions.get(adminId);
        if (oldSession != null && oldSession.isOpen()) {
            try {
                oldSession.close();
            } catch (IOException e) {
                log.error("关闭旧WebSocket连接失败", e);
            }
        }
        adminSessions.put(adminId, session);
        log.info("管理员上线: adminId={}, 当前在线: {}", adminId, adminSessions.size());
    }

    /**
     * 移除管理员会话
     */
    public void removeAdminSession(Integer adminId) {
        adminSessions.remove(adminId);
        log.info("管理员下线: adminId={}, 当前在线: {}", adminId, adminSessions.size());
    }

    /**
     * 获取管理员会话
     */
    public WebSocketSession getAdminSession(Integer adminId) {
        return adminSessions.get(adminId);
    }

    /**
     * 判断管理员是否在线
     */
    public boolean isAdminOnline(Integer adminId) {
        WebSocketSession session = adminSessions.get(adminId);
        return session != null && session.isOpen();
    }

    /**
     * 获取所有在线管理员ID
     */
    public Set<Integer> getOnlineAdminIds() {
        return adminSessions.keySet();
    }

    /**
     * 获取在线管理员数量
     */
    public int getOnlineAdminCount() {
        return adminSessions.size();
    }

    /**
     * 获取在线用户数量（契约补齐：Dashboard 在线人数统计）
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 发送消息给指定管理员
     */
    public void sendToAdmin(Integer adminId, String message) {
        WebSocketSession session = adminSessions.get(adminId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("发送WebSocket消息失败: adminId={}", adminId, e);
            }
        }
    }

    /**
     * 广播消息给所有在线管理员
     */
    public void broadcastToAllAdmins(String message) {
        adminSessions.forEach((adminId, session) -> {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(message));
                    }
                } catch (IOException e) {
                    log.error("广播WebSocket消息失败: adminId={}", adminId, e);
                }
            }
        });
    }

    /**
     * 发送消息给指定管理员列表
     */
    public void sendToAdmins(Set<Integer> adminIds, String message) {
        for (Integer adminId : adminIds) {
            sendToAdmin(adminId, message);
        }
    }

    /**
     * 加入对话
     */
    public void joinConversation(Long conversationId, Integer adminId) {
        conversationAdmins.computeIfAbsent(conversationId, k -> ConcurrentHashMap.newKeySet())
                .add(adminId);
        log.debug("管理员加入对话: adminId={}, conversationId={}", adminId, conversationId);
    }

    /**
     * 离开对话
     */
    public void leaveConversation(Long conversationId, Integer adminId) {
        Set<Integer> admins = conversationAdmins.get(conversationId);
        if (admins != null) {
            admins.remove(adminId);
            log.debug("管理员离开对话: adminId={}, conversationId={}", adminId, conversationId);
        }
    }

    /**
     * 获取对话的所有在线管理员
     */
    public Set<Integer> getConversationAdmins(Long conversationId) {
        return conversationAdmins.getOrDefault(conversationId, Set.of());
    }

    /**
     * 添加用户会话
     */
    public void addUserSession(Integer userId, WebSocketSession session) {
        WebSocketSession oldSession = userSessions.get(userId);
        if (oldSession != null && oldSession.isOpen()) {
            try {
                oldSession.close();
            } catch (IOException e) {
                log.error("关闭旧用户WebSocket连接失败", e);
            }
        }
        userSessions.put(userId, session);
        log.info("用户上线: userId={}, 当前在线: {}", userId, userSessions.size());
    }

    /**
     * 移除用户会话
     */
    public void removeUserSession(Integer userId) {
        userSessions.remove(userId);
        log.info("用户下线: userId={}, 当前在线: {}", userId, userSessions.size());
    }

    /**
     * 发送消息给用户
     */
    public void sendToUser(Integer userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("发送WebSocket消息给用户失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 设置用户当前选择的会话ID
     */
    public void setUserCurrentSessionId(Integer userId, String sessionId) {
        userCurrentSessionIds.put(userId, sessionId);
        log.debug("设置用户当前会话ID: userId={}, sessionId={}", userId, sessionId);
    }

    /**
     * 获取用户当前选择的会话ID
     */
    public String getUserCurrentSessionId(Integer userId) {
        return userCurrentSessionIds.get(userId);
    }

    /**
     * 清除用户当前选择的会话ID
     */
    public void clearUserCurrentSessionId(Integer userId) {
        userCurrentSessionIds.remove(userId);
        log.debug("清除用户当前会话ID: userId={}", userId);
    }
}
