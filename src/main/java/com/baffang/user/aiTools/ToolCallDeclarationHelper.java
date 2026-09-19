package com.baffang.user.aiTools;

/**
 * 工具调用声明助手类
 * 用于在AI工具调用时向用户发送调用声明
 */
public class ToolCallDeclarationHelper {

    /**
     * 发送工具调用声明
     *
     * @param toolName 工具名称
     */
    public void sendToolCallDeclaration(String toolName) {
        sendToolCallDeclaration(toolName, "正在执行" + toolName + "...");
    }

    /**
     * 发送工具调用声明（自定义消息）
     *
     * @param toolName 工具名称
     * @param message  自定义消息
     */
    public void sendToolCallDeclaration(String toolName, String message) {
        System.out.println("发送工具调用声明: " + toolName + ", 消息: " + message);
    }
}
