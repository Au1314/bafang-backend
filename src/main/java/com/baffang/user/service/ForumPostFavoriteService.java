package com.baffang.user.service;

/**
 * 用户端帖子收藏服务接口
 */
public interface ForumPostFavoriteService {

    /**
     * 收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    Boolean favoritePost(Integer userId, Integer postId);

    /**
     * 取消收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    Boolean cancelFavoritePost(Integer userId, Integer postId);

    /**
     * 获取用户收藏帖子列表
     * 
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页的收藏帖子列表
     */
    com.baffang.common.result.PageResult<com.baffang.user.vo.ForumPostListVO> getUserFavoritePosts(Integer userId,
            Integer pageNum, Integer pageSize);
}