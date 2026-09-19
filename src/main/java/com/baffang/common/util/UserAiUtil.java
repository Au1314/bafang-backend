package com.baffang.common.util;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class UserAiUtil {

    @Autowired
    private ChatClient chatClient;

    /**
     * 根据提示词向AI发送请求并返回结果
     *
     * @param prompt 提示词
     * @return AI的响应结果
     */
    public String sendMessage(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 根据提示词向AI发送请求并返回结果
     *
     * @param prompt 提示词
     * @return AI的响应结果
     */
    public String sendMessage(String prompt, double temperature) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 根据系统提示词和用户提示词向AI发送请求并返回结果
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI的响应结果
     */
    public String sendMessage(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     *
     * @param prompt 提示词
     * @return AI的流式响应结果
     */
    public Flux<String> sendStreamMessage(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

    /**
     * 根据系统提示词和用户提示词向AI发送请求并返回流式响应结果
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI的流式响应结果
     */
    public Flux<String> sendStreamMessage(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .stream()
                .content();
    }

    /**
     * 根据系统提示词和用户提示词向AI发送请求并返回流式响应结果
     *
     * @param systemPrompt  系统提示词
     * @param userPrompt    用户提示词
     * @param toolCallbacks 工具调用列表
     * @return AI的流式响应结果
     */
    public Flux<String> sendStreamMessage(String systemPrompt, String userPrompt, List<ToolCallback> toolCallbacks) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .tools(toolCallbacks)
                .stream()
                .content();
    }

    /**
     * 根据系统提示词、用户当前消息和对话历史向AI发送请求并返回流式响应结果
     *
     * @param systemPrompt       系统提示词
     * @param currentUserMessage 用户当前消息
     * @param messageHistory     对话历史
     * @param toolCallbacks      工具调用列表
     * @return AI的流式响应结果
     */
    public Flux<String> sendStreamMessageWithHistory(String systemPrompt, String currentUserMessage,
            List<Message> messageHistory, List<ToolCallback> toolCallbacks) {
        return chatClient.prompt()
                .system(systemPrompt)
                .messages(messageHistory)
                .user(currentUserMessage)
                .tools(toolCallbacks)
                .stream()
                .content();
    }

    /**
     * 根据系统提示词和用户提示词向AI发送请求并返回流式响应结果
     *
     * @param systemPrompt  系统提示词
     * @param userPrompt    用户提示词
     * @param toolCallbacks 工具调用列表
     * @return AI的非流式响应结果
     */
    public String sendMessage(String systemPrompt, String userPrompt, List<ToolCallback> toolCallbacks) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .tools(toolCallbacks)
                .call()
                .content();
    }
}
