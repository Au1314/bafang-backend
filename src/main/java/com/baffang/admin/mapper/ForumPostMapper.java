package com.baffang.admin.mapper;

import com.baffang.admin.dto.ForumPostQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ForumPostMapper {

    /**
     * 分页查询帖子列表
     */
    List<Map<String, Object>> selectList(@Param("query") ForumPostQueryDTO query);

    /**
     * 查询帖子详情
     */
    Map<String, Object> selectDetailById(@Param("id") Integer id);

    /**
     * 检查帖子是否存在
     */
    boolean existsById(@Param("id") Integer id);

    /**
     * 逻辑删除帖子
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 批量逻辑删除帖子
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 设置置顶状态
     */
    int updateTop(@Param("id") Integer id, @Param("isTop") Integer isTop);

    /**
     * 设置精华状态
     */
    int updateEssence(@Param("id") Integer id, @Param("isEssence") Integer isEssence);

    /**
     * 查询帖子统计
     */
    Map<String, Object> selectStatistics();

    /**
     * 查询帖子的评论数
     */
    Integer selectReplyCount(@Param("postId") Integer postId);


    /**
 * 删除帖子下的所有评论
 */
int deleteRepliesByPostId(@Param("postId") Integer postId);

/**
 * 批量删除帖子下的所有评论
 */
int batchDeleteRepliesByPostIds(@Param("postIds") List<Integer> postIds);

}
