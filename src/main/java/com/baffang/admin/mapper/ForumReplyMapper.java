package com.baffang.admin.mapper;

import com.baffang.admin.dto.ForumReplyQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ForumReplyMapper {

    /**
     * 分页查询评论列表
     */
    List<Map<String, Object>> selectList(@Param("query") ForumReplyQueryDTO query);

    /**
     * 查询评论详情
     */
    Map<String, Object> selectDetailById(@Param("id") Integer id);

    /**
     * 检查评论是否存在
     */
    boolean existsById(@Param("id") Integer id);

    /**
     * 逻辑删除评论
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 批量逻辑删除评论
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 查询帖子的最新评论
     */
    List<Map<String, Object>> selectLatestByPostId(@Param("postId") Integer postId, @Param("limit") Integer limit);

    /**
     * 查询子评论
     */
    List<Map<String, Object>> selectChildReplies(@Param("parentId") Integer parentId);

    /**
     * 查询评论统计
     */
    Map<String, Object> selectStatistics();
}
