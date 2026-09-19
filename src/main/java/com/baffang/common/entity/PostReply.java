package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 论坛回复表实体（对帖子的回复，以及对回复的回复）
 */
@Data
public class PostReply {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 帖子ID(关联帖子表id)
     */
    private Integer postId;

    /**
     * 回复用户ID(关联用户表id)
     */
    private Integer userId;

    /**
     * 父回复ID，NULL表示直接回复帖子
     */
    private Integer parentId;

    /**
     * 被回复用户ID(关联用户表id)
     */
    private Integer replyToUserId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 点赞数（默认0）
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer isDeleted;
}