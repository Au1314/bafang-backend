package com.baffang.user.service;

import com.baffang.common.entity.ForumPost;
import com.baffang.common.result.PageResult;
import com.baffang.user.vo.ForumPostListVO;

/**
 * 用户端帖子服务接口
 */
public interface ForumPostService {

    /**
     * 发布帖子
     * 
     * @param forumPost 帖子实体
     * @return 帖子ID
     */
    Integer publishPost(ForumPost forumPost);

    /**
     * 获取帖子列表
     * 
     * @param pageNum  页码，默认1
     * @param pageSize 每页条数，默认10
     * @param sort     排序方式：new=最新发布 /hot=热门（即点赞数）
     * @param keyword  关键字搜索
     * @return 帖子列表
     */
    PageResult<ForumPostListVO> getPostList(Integer pageNum, Integer pageSize, String sort, String keyword);

    /**
     * 点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 更新后的点赞数
     */
    Integer likePost(Integer userId, Integer postId);

    /**
     * 取消点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 取消点赞后的点赞数
     */
    Integer cancelLikePost(Integer userId, Integer postId);

    /**
     * 检查帖子是否存在
     * 
     * @param postId 帖子ID
     * @return 是否存在
     */
    Boolean checkPostExists(Integer postId);

    /**
     * 获取帖子详情（含回复列表）
     * 
     * @param postId 帖子ID
     * @param userId 用户ID（可选，用于判断当前用户是否已点赞和收藏）
     * @return 帖子详情VO
     */
    com.baffang.user.vo.PostDetailResponseVO getPostDetail(Integer postId, Integer userId);
}