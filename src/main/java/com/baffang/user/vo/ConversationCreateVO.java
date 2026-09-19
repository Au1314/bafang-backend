package com.baffang.user.vo;

import lombok.Data;

/**
 * 对话创建响应VO
 */
@Data
public class ConversationCreateVO {
    /**
     * 对话ID
     */
    private Integer conversationId;

    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 桩主用户ID
     */
    private Integer ownerId;

    /**
     * 桩主名称
     */
    private String ownerName;
}