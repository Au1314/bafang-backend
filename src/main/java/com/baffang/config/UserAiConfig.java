package com.baffang.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAiConfig {
    /**
     * 普通对话聊天功能的模型
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        // ChatClient chatClient = ChatClient.create(model); // 简单创建实例
        // 或使用builder创建更高级控制
        return ChatClient
                .builder(model) // 也可以使用ChatClient.Builder，利用build获取ChatClient对象
                .build();
    }
}
