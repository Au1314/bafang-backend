package com.baffang.user.dto;

import lombok.Data;

/**
 * 发送消息请求DTO
 */
@Data
public class SendMessageDTO {
    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：0 = 文本，1 = 图片
     * 默认值: 0
     */
    private Integer messageType = 0;

    /**
     * 扩展信息（如图片 URL）
     */
    private String extraInfo;
}