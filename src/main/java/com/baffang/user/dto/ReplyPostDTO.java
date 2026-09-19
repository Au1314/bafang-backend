package com.baffang.user.dto;

import lombok.Data;

/**
 * 回复帖子请求参数DTO
 */
@Data
public class ReplyPostDTO {
    
    /**
     * 目标帖子ID
     */
    private Integer postId;
    
    /**
     * 父回复ID，null表示回复帖子，非null表示回复某条回复
     */
    private Integer parentId;
    
    /**
     * 被回复用户ID，回复用户时必填
     */
    private Integer targetUserId;
    
    /**
     * 回复内容
     */
    private String content;
}