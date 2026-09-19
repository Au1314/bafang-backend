package com.baffang.user.mapper;

import com.baffang.common.entity.PostFavorite;
import com.baffang.user.vo.ForumPostListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户端帖子收藏Mapper
 */
@Mapper
public interface UserForumPostFavoriteMapper {

    /**
     * 查询用户是否已收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已收藏
     */
    Integer checkUserFavoritePost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    /**
     * 插入收藏记录
     * 
     * @param postFavorite 收藏实体
     * @return 影响行数
     */
    int insertPostFavorite(PostFavorite postFavorite);

    /**
     * 更新帖子收藏数（增加）
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int increasePostCollectCount(@Param("postId") Integer postId);

    /**
     * 更新帖子收藏数（减少）
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int decreasePostCollectCount(@Param("postId") Integer postId);

    /**
     * 取消收藏（将is_deleted设为1）
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 影响行数
     */
    int cancelFavoritePost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    /**
     * 获取用户收藏帖子列表
     * 
     * @param userId 用户ID
     * @return 收藏帖子列表
     */
    List<ForumPostListVO> getUserFavoritePosts(@Param("userId") Integer userId);
}