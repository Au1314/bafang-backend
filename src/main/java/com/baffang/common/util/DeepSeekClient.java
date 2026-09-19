package com.baffang.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 客户端
 */
@Slf4j
@Component
public class DeepSeekClient {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.api.timeout:120}")
    private int timeout;

    private OkHttpClient client;
    private final Gson gson = new Gson();

    /**
     * 初始化 OkHttpClient
     */
    @PostConstruct
    public void init() {
        log.info("初始化 DeepSeekClient...");
        log.info("API URL: {}", apiUrl);
        log.info("Model: {}", model);
        log.info("Timeout: {}s", timeout);
        log.info("API Key: {}", apiKey != null && apiKey.length() > 10 
                ? apiKey.substring(0, 10) + "..." : "未配置");

        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        log.info("DeepSeekClient 初始化完成");
    }

    /**
     * 发送聊天请求
     */
    public String chat(String systemPrompt, String userMessage) {
        if (client == null) {
            log.error("OkHttpClient 未初始化，尝试重新初始化...");
            init();
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("DeepSeek API Key 未配置，请在 application.yml 中配置 deepseek.api.key");
        }

        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        JsonArray messages = new JsonArray();

        // System message
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);

        // User message
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 4096);

        String jsonBody = gson.toJson(requestBody);
        log.debug("请求体: {}", jsonBody);

        // 构建 HTTP 请求
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("DeepSeek API 调用失败: code={}, body={}", response.code(), errorBody);
                throw new RuntimeException("DeepSeek API 调用失败: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            log.debug("响应体: {}", responseBody);

            // 解析响应
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray choices = jsonResponse.getAsJsonArray("choices");

            if (choices != null && choices.size() > 0) {
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                JsonObject message = firstChoice.getAsJsonObject("message");
                return message.get("content").getAsString();
            }

            throw new RuntimeException("DeepSeek API 响应格式错误: " + responseBody);

        } catch (IOException e) {
            log.error("DeepSeek API 请求异常", e);
            throw new RuntimeException("DeepSeek API 请求异常: " + e.getMessage(), e);
        }
    }

    /**
     * 简单聊天（无系统提示）
     */
    public String chat(String userMessage) {
        return chat("你是一个有帮助的助手。", userMessage);
    }
}
