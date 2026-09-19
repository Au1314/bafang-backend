package com.baffang.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 帖子列表项VO
 */
@Data
public class ForumPostListVO {

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 图片列表
     */
    private List<String> images;

    /**
     * 发布者用户名
     */
    private String publisherName;

    /**
     * 发布者头像URL
     */
    private String publisherAvatar;
}