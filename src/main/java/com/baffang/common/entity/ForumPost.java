package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 车辆论坛帖子表实体
 */
@Data
public class ForumPost {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 帖子作者ID(关联用户表id)
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
     * 点赞数（默认0）
     */
    private Integer likeCount;

    /**
     * 收藏数（默认0）
     */
    private Integer collectCount;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 是否精华：0-否，1-是
     */
    private Integer isEssence;

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

    /**
     * 帖子图片列表（JSON格式存储）
     */
    private String images;
}