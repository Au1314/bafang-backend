package com.baffang.user.websocket;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.entity.AIChatConversation;
import com.baffang.common.entity.AIChatMessage;
import com.baffang.common.result.AiChatResult;
import com.baffang.common.util.UserAiUtil;
import com.baffang.user.aiTools.*;
import com.baffang.user.service.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI聊天WebSocket处理器
 */
@Slf4j
@Component
public class AIChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private UserAiUtil userAiUtil;

    @Autowired
    private PrivateStationUseService privateStationUseService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private StationSearchService stationSearchService;

    @Autowired
    private PrivateStationService privateStationService;

    @Autowired
    private PublicStationService publicStationService;

    @Autowired
    private CarAuthService carAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AIChatConversationService aiChatConversationService;

    @Autowired
    private AIChatMessageService aiChatMessageService;

    @Autowired
    private com.baffang.user.service.AiKnowledgeRetrievalService knowledgeRetrievalService;

    /**
     * 连接建立时触发
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");

        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        // 保存用户会话
        sessionManager.addUserSession(userId, session);

        // 发送连接成功消息
        AiChatResult connectedResult = new AiChatResult();
        connectedResult.source = 2; // 来自系统
        connectedResult.message = "AI聊天连接已建立，您可以开始提问了。";

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connectedResult)));
        log.info("AI聊天WebSocket连接建立: userId={}, sessionId={}", userId, session.getId());
    }

    /**
     * 接收客户端消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");
        String payload = message.getPayload();

        log.info("收到AI聊天WebSocket消息: userId={}, message={}", userId, payload);

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "";

            switch (type) {
                case "CHAT_MESSAGE":
                    handleChatMessage(session, userId, jsonNode);
                    break;

                case "PING":
                    handlePing(session);
                    break;

                default:
                    log.warn("未知的AI聊天消息类型: {}", type);
                    sendErrorMessage(session, "未知的消息类型: " + type);
            }

        } catch (Exception e) {
            log.error("处理AI聊天WebSocket消息失败", e);
            sendErrorMessage(session, "消息处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(WebSocketSession session, Integer userId, JsonNode jsonNode) {
        if (!jsonNode.has("message")) {
            sendErrorMessage(session, "缺少消息内容");
            return;
        }

        String userMessage = jsonNode.get("message").asText();

        // 获取或创建会话ID
        // 1. 首先检查用户会话中是否保存了当前会话ID
        String savedSessionId = sessionManager.getUserCurrentSessionId(userId);
        // 2. 如果有，优先使用该会话ID；如果没有，再检查消息中是否携带了sessionId；如果都没有，使用默认的sessionId
        String sessionId = savedSessionId != null ? savedSessionId
                : (jsonNode.has("sessionId") ? jsonNode.get("sessionId").asText() : session.getId());

        // 获取或创建对话会话
        final AIChatConversation conversation;
        AIChatConversation tempConversation = aiChatConversationService.getConversationBySessionId(sessionId);
        if (tempConversation == null) {
            tempConversation = aiChatConversationService.createConversation(userId, "deepseek-chat", sessionId);
        }
        conversation = tempConversation;

        // 保存用户发送的消息
        aiChatMessageService.saveUserMessage(conversation.getId(), userMessage);

        // 更新对话会话的最后交互时间
        aiChatConversationService.updateLastInteractionTime(conversation.getId());

        // 准备工具列表
        List<ToolCallback> toolCallbacks = new ArrayList<>();

        // 创建新的DateTimeTools实例并传入参数
        DateTimeTools dateTimeTools = new DateTimeTools(userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, dateTimeTools);

        // 创建新的PrivateStationReserveTools实例并传入参数
        PrivateStationReserveTools privateStationReserveTools = new PrivateStationReserveTools(privateStationUseService,
                conversationService, userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, privateStationReserveTools);

        // 创建新的StationSearchTools实例并传入参数
        StationSearchTools stationSearchTools = new StationSearchTools(stationSearchService, userId, sessionManager,
                objectMapper);
        addToolToList(toolCallbacks, stationSearchTools);

        // 创建新的PrivateStationDetailTools实例并传入参数
        PrivateStationDetailTools privateStationDetailTools = new PrivateStationDetailTools(privateStationService,
                userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, privateStationDetailTools);

        // 创建新的PrivateStationTools实例并传入参数
        PrivateStationTools privateStationTools = new PrivateStationTools(privateStationService, userId, sessionManager,
                objectMapper);
        addToolToList(toolCallbacks, privateStationTools);

        // 创建新的PublicStationDetailTools实例并传入参数
        PublicStationDetailTools publicStationDetailTools = new PublicStationDetailTools(publicStationService,
                userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, publicStationDetailTools);

        // 创建新的UpdatePrivateStationTools实例并传入参数
        UpdatePrivateStationTools updatePrivateStationTools = new UpdatePrivateStationTools(privateStationService,
                userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, updatePrivateStationTools);

        // 创建新的CarAuthTools实例并传入参数
        CarAuthTools carAuthTools = new CarAuthTools(carAuthService, userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, carAuthTools);

        // 创建新的FillCarAuthInfoTools实例并传入参数
        FillCarAuthInfoTools fillCarAuthInfoTools = new FillCarAuthInfoTools(userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, fillCarAuthInfoTools);

        // 创建新的GetUserInfoById实例并传入参数
        GetUserInfoById getUserInfoById = new GetUserInfoById(userService, userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, getUserInfoById);

        // 创建新的ManageStationOperateStatusTools实例并传入参数
        ManageStationOperateStatusTools manageStationOperateStatusTools = new ManageStationOperateStatusTools(
                privateStationService, userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, manageStationOperateStatusTools);

        // 创建新的UserInfoWSTools实例并传入参数
        UserInfoWSTools userInfoWSTools = new UserInfoWSTools(userService, userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, userInfoWSTools);

        // 创建新的StationInfoTools实例并传入参数
        StationInfoTools stationInfoTools = new StationInfoTools(privateStationService, publicStationService,
                userId, sessionManager, objectMapper);
        addToolToList(toolCallbacks, stationInfoTools);

        // 异步处理AI流式响应
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();

                // 获取对话历史
                List<AIChatMessage> messageHistory = aiChatMessageService
                        .getMessageHistoryByConversationId(conversation.getId());

                // 将对话历史转换为Message对象列表
                List<Message> chatHistory = new ArrayList<>();
                for (AIChatMessage message : messageHistory) {
                    if (message.getSenderType() == 0) { // 0-用户
                        chatHistory.add(new UserMessage(message.getContent()));
                    } else if (message.getSenderType() == 1) { // 1-AI
                        chatHistory.add(new AssistantMessage(message.getContent()));
                    }
                }

                // RAG-lite：检索与用户问题相关的论坛帖/平台公告，注入系统提示词
                // （DeepSeek 无 embedding 接口，采用关键词检索；检索失败返回空串不影响主流程）
                String basePrompt = "你现在是八方互充平台用户的一名助手，将协助他完成各种任务，如果当前的工具无法满足用户需求，要明确告诉用户无法做到并提出建议,注意：1.应始终以中文回复  2.尽量少涉及系统业务字段，例如经纬度应直接以“经纬度”向用户描述，而非lat和lng   3.每次使用toolcall功能调用tool之前，都务必确保已经说完整个句子，不要遗留几个字符或标点符号  4.需考虑如何使用工具为用户提供更好的服务（例如用户寻找充电桩时，如果你找到了，则可以用getStationInfo工具直接将其展示给用户）  ";
                String knowledge = knowledgeRetrievalService.retrieve(userMessage, 5);
                String systemPrompt = knowledge.isEmpty() ? basePrompt
                        : basePrompt
                                + "\n\n【平台知识库检索结果】以下是平台论坛帖与公告中可能与本次问题相关的内容，回答相关问题时可参考引用；如与问题无关请忽略，不要编造：\n"
                                + knowledge;

                // 调用AI流式接口，传递对话历史
                Flux<String> aiStream = userAiUtil.sendStreamMessageWithHistory(
                        systemPrompt,
                        userMessage,
                        chatHistory,
                        toolCallbacks);

                // 处理流式响应
                TokenBuffer tokenBuffer = new TokenBuffer(10, 30);
                StringBuilder fullResponseBuilder = new StringBuilder(); // 收集完整的AI响应
                AtomicBoolean isError = new AtomicBoolean(false);

                aiStream.subscribe(
                        text -> {
                            try {
                                fullResponseBuilder.append(text);
                                tokenBuffer.append(text);
                                if (tokenBuffer.shouldFlush()) {
                                    sendAIChatResult(session, tokenBuffer.flush());
                                }
                            } catch (Exception e) {
                                log.error("处理AI流式响应失败", e);
                                isError.set(true);
                            }
                        },
                        error -> {
                            log.error("AI流式响应发生错误", error);
                            sendErrorMessage(session, "AI回复失败: " + error.getMessage());
                            isError.set(true);
                        },
                        () -> {
                            try {
                                // 发送剩余的内容
                                if (!tokenBuffer.isEmpty()) {
                                    String remainingText = tokenBuffer.flush();
                                    fullResponseBuilder.append(remainingText);
                                    sendAIChatResult(session, remainingText);
                                }
                                // 发送结束标记
                                if (!isError.get()) {
                                    sendStreamEndMessage(session);

                                    // 计算响应时间
                                    long responseTime = System.currentTimeMillis() - startTime;

                                    // 保存完整的AI回复
                                    aiChatMessageService.saveAIMessage(conversation.getId(),
                                            fullResponseBuilder.toString(), (int) responseTime);

                                    // 更新消息总数
                                    aiChatConversationService.incrementMessageCount(conversation.getId());
                                }
                            } catch (Exception e) {
                                log.error("发送AI流式响应结束消息失败", e);
                            }
                        });
            } catch (Exception e) {
                log.error("调用AI流式接口失败", e);
                sendErrorMessage(session, "AI服务调用失败: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 处理PING消息
     */
    private void handlePing(WebSocketSession session) throws Exception {
        AiChatResult pongResult = new AiChatResult();
        pongResult.source = 2;
        pongResult.message = "pong";
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongResult)));
    }

    /**
     * 发送AI聊天结果
     */
    private void sendAIChatResult(WebSocketSession session, String message) throws Exception {
        AiChatResult result = new AiChatResult();
        result.source = 1; // 来自AI回复
        result.toolCallReturnType = null; // AI回复时该字段为空
        result.message = message;
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
    }

    /**
     * 发送流结束消息
     */
    private void sendStreamEndMessage(WebSocketSession session) throws Exception {
        AiChatResult endResult = new AiChatResult();
        endResult.source = 1;
        endResult.message = "[STREAM_END]";
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(endResult)));
    }

    /**
     * 发送错误消息
     */
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            AiChatResult errorResult = new AiChatResult();
            errorResult.source = 2;
            errorResult.message = errorMessage;
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResult)));
        } catch (Exception e) {
            log.error("发送错误消息失败", e);
        }
    }

    /**
     * 添加工具到列表
     */
    private void addToolToList(List<ToolCallback> toolCallbacks, Object tool) {
        ToolCallback[] toolCallbackArray = ToolCallbacks.from(tool);
        for (ToolCallback toolCallback : toolCallbackArray) {
            toolCallbacks.add(toolCallback);
        }
    }

    /**
     * 连接关闭时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");

        if (userId != null) {
            sessionManager.removeUserSession(userId);
            // 断开连接时清除用户当前保存的会话ID，重新连接后将创建新对话
            sessionManager.clearUserCurrentSessionId(userId);
        }

        log.info("AI聊天WebSocket连接关闭: userId={}, status={}", userId, status);
    }

    /**
     * 处理传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.error("AI聊天WebSocket传输错误: userId={}", userId, exception);

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 令牌缓冲区，用于拼接流式响应
     */
    static class TokenBuffer {
        private final int minTokenCount;
        private final int maxTokenCount;
        private final StringBuilder buffer = new StringBuilder();
        private int tokenCount = 0;

        public TokenBuffer(int minTokenCount, int maxTokenCount) {
            this.minTokenCount = minTokenCount;
            this.maxTokenCount = maxTokenCount;
        }

        public void append(String text) {
            buffer.append(text);
            tokenCount += countTokens(text);
        }

        public boolean shouldFlush() {
            return tokenCount >= minTokenCount;
        }

        public String flush() {
            String result = buffer.toString();
            buffer.setLength(0);
            tokenCount = 0;
            return result;
        }

        public boolean isEmpty() {
            return buffer.length() == 0;
        }

        /**
         * 简单的token计数算法
         * 中文每个字符算一个token
         * 英文按空格分隔算token
         */
        private int countTokens(String text) {
            if (text == null || text.isEmpty()) {
                return 0;
            }

            int count = 0;
            StringBuilder englishWord = new StringBuilder();

            for (char c : text.toCharArray()) {
                if (isChineseChar(c)) {
                    // 处理缓存的英文单词
                    if (englishWord.length() > 0) {
                        count++;
                        englishWord.setLength(0);
                    }
                    // 中文字符算一个token
                    count++;
                } else if (Character.isLetterOrDigit(c)) {
                    // 积累英文字符
                    englishWord.append(c);
                } else if (Character.isWhitespace(c)) {
                    // 空格分隔英文单词
                    if (englishWord.length() > 0) {
                        count++;
                        englishWord.setLength(0);
                    }
                } else {
                    // 标点符号单独算一个token
                    if (englishWord.length() > 0) {
                        count++;
                        englishWord.setLength(0);
                    }
                    count++;
                }
            }

            // 处理最后一个英文单词
            if (englishWord.length() > 0) {
                count++;
            }

            return count;
        }

        /**
         * 判断是否为中文字符
         */
        private boolean isChineseChar(char c) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
            return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION;
        }
    }
}
