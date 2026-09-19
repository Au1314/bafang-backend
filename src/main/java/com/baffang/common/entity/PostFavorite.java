package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 帖子收藏表实体
 */
@Data
public class PostFavorite {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户ID(关联用户表id)
     */
    private Integer userId;

    /**
     * 帖子ID(关联帖子表id)
     */
    private Integer postId;

    /**
     * 收藏时间
     */
    private Date createTime;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer isDeleted;
}