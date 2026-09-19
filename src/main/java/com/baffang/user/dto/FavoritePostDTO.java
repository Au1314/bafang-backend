package com.baffang.user.dto;

import lombok.Data;

/**
 * 收藏帖子请求参数DTO
 */
@Data
public class FavoritePostDTO {
    
    /**
     * 帖子ID
     */
    private Integer postId;
}