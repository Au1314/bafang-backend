package com.baffang.common.result;

public class AiChatResult {
    /*
     * 来源
     * 1：来自Ai回复
     * 2：来自ToolCall返回
     */
    public Integer source;
    /**
     * ToolCall返回类型
     * 1: 调用声明（即ToolCall向前端返回消息指明Ai在调用该参数）
     * 2：直接返回信息（即ToolCall直接返回调用结果给用户，不经过Ai）
     * Ai回复时该字段为空
     */
    public Integer toolCallReturnType;
    /**
     * 调用的工具名称
     */
    public String toolName;
    /**
     * 常用于调用声明信息以及Ai直接返回的信息
     */
    public String message;
    /**
     * 常用于存储ToolCall直接返回信息
     */
    public Object data;

}
