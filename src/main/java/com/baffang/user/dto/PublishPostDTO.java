package com.baffang.user.dto;

import lombok.Data;

/**
 * 发布帖子请求参数DTO
 */
@Data
public class PublishPostDTO {
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 帖子图片列表
     */
    private java.util.List<String> images;
}