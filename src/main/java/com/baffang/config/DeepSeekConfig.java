package com.baffang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek AI 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.deepseek")
public class DeepSeekConfig {
    
    /**
     * API Key
     */
    private String apiKey;
    
    /**
     * API Base URL
     */
    private String baseUrl = "https://api.deepseek.com";
    
    /**
     * 模型名称
     */
    private String model = "deepseek-chat";
    
    /**
     * 请求超时时间(秒)
     */
    private int timeout = 120;
}
