package com.baffang.user.controller;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.service.AIChatConversationService;
import com.baffang.user.service.AIChatMessageService;
import com.baffang.user.vo.AIChatConversationListVO;
import com.baffang.user.vo.AIChatMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI对话控制器
 */
@RestController
@RequestMapping("/api/v1/user/ai")
public class AIChatController {

    @Autowired
    private AIChatConversationService aiChatConversationService;

    @Autowired
    private AIChatMessageService aiChatMessageService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    /**
     * 获取用户的AI对话列表
     *
     * @param authorization 请求头中的Authorization令牌
     * @return 对话列表
     */
    @GetMapping("/conversations")
    public Result<List<AIChatConversationListVO>> getConversationList(
            @RequestHeader("Authorization") String authorization) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取用户的AI对话列表
        List<AIChatConversationListVO> conversationList = aiChatConversationService.getConversationListByUserId(userId);

        // 4. 返回结果
        Result<List<AIChatConversationListVO>> result = Result.success(conversationList);
        result.setMsg("获取会话列表成功");
        return result;
    }

    /**
     * 获取指定会话的消息历史记录
     *
     * @param authorization 请求头中的Authorization令牌
     * @param sessionId     会话ID
     * @return 消息历史记录
     */
    @GetMapping("/conversations/{sessionId}/messages")
    public Result<List<AIChatMessageVO>> getMessageHistory(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("sessionId") String sessionId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 根据会话ID获取对话会话
        com.baffang.common.entity.AIChatConversation conversation = aiChatConversationService
                .getConversationBySessionId(sessionId);

        // 4. 检查该对话是否属于当前用户
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            Result<List<AIChatMessageVO>> result = Result.error("会话不存在或不属于当前用户");
            return result;
        }

        // 5. 调用Service层方法获取会话的消息历史记录
        List<AIChatMessageVO> messageHistory = aiChatMessageService
                .getMessageHistoryVOByConversationId(conversation.getId());

        // 6. 返回结果
        Result<List<AIChatMessageVO>> result = Result.success(messageHistory);
        result.setMsg("获取会话历史记录成功");
        return result;
    }

    /**
     * 切换到指定会话
     *
     * @param authorization 请求头中的Authorization令牌
     * @param sessionId     会话ID
     * @return 切换结果
     */
    @GetMapping("/conversations/{sessionId}/switch")
    public Result<?> switchConversation(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("sessionId") String sessionId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 根据会话ID获取对话会话
        com.baffang.common.entity.AIChatConversation conversation = aiChatConversationService
                .getConversationBySessionId(sessionId);

        // 4. 检查该对话是否属于当前用户
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            Result<?> result = Result.error("会话不存在或不属于当前用户");
            return result;
        }

        // 5. 如果会话状态不是进行中，更新为进行中
        if (conversation.getStatus() != 0) {
            aiChatConversationService.updateStatus(conversation.getId(), 0);
        }

        // 6. 保存用户当前选择的会话ID
        sessionManager.setUserCurrentSessionId(userId, sessionId);

        // 7. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("切换会话成功");
        return result;
    }
}