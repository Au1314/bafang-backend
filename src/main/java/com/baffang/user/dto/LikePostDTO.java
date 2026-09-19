package com.baffang.user.dto;

import lombok.Data;

/**
 * 点赞帖子请求参数DTO
 */
@Data
public class LikePostDTO {
    
    /**
     * 帖子ID
     */
    private Integer postId;
}