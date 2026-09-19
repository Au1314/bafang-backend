package com.baffang.user.mapper;

import com.baffang.common.entity.ForumPost;
import com.baffang.user.vo.ForumPostListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户端帖子Mapper
 */
@Mapper
public interface UserForumPostMapper {

    /**
     * 插入帖子
     * 
     * @param forumPost 帖子实体
     * @return 影响行数
     */
    int insertPost(ForumPost forumPost);

    /**
     * 获取帖子列表
     * 
     * @param sort    排序方式：new=最新发布 /hot=热门（即点赞数）
     * @param keyword 关键字搜索
     * @return 帖子列表
     */
    List<java.util.Map<String, Object>> getPostList(@Param("sort") String sort, @Param("keyword") String keyword);

    /**
     * 更新帖子点赞数（增加）
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int increasePostLikeCount(@Param("postId") Integer postId);

    /**
     * 获取帖子当前点赞数
     * 
     * @param postId 帖子ID
     * @return 当前点赞数
     */
    Integer getPostLikeCount(@Param("postId") Integer postId);

    /**
     * 检查帖子是否存在
     * 
     * @param postId 帖子ID
     * @return 存在返回1，不存在返回0
     */
    Integer checkPostExists(@Param("postId") Integer postId);

    /**
     * 查询帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情VO
     */
    com.baffang.user.vo.PostDetailResponseVO.PostVO getPostDetail(@Param("postId") Integer postId);

    /**
     * 查询帖子详情（返回Map）
     * 
     * @param postId 帖子ID
     * @return 帖子详情Map
     */
    java.util.Map<String, Object> selectPostDetailMap(@Param("postId") Integer postId);
}