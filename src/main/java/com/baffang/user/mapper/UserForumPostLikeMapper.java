package com.baffang.user.mapper;

import com.baffang.common.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户端帖子点赞Mapper
 */
@Mapper
public interface UserForumPostLikeMapper {

    /**
     * 查询用户是否已点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 点赞记录数
     */
    Integer checkUserLikePost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    /**
     * 插入点赞记录
     * 
     * @param postLike 点赞实体
     * @return 影响行数
     */
    int insertPostLike(PostLike postLike);

    /**
     * 更新帖子点赞数（增加）
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int increasePostLikeCount(@Param("postId") Integer postId);

    /**
     * 更新帖子点赞数（减少）
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int decreasePostLikeCount(@Param("postId") Integer postId);

    /**
     * 获取帖子当前点赞数
     * 
     * @param postId 帖子ID
     * @return 当前点赞数
     */
    Integer getPostLikeCount(@Param("postId") Integer postId);

    /**
     * 取消点赞（将is_deleted设为1）
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 影响行数
     */
    int cancelPostLike(@Param("userId") Integer userId, @Param("postId") Integer postId);
}