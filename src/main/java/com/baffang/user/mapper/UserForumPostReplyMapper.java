package com.baffang.user.mapper;

import com.baffang.common.entity.PostReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户端帖子回复Mapper
 */
@Mapper
public interface UserForumPostReplyMapper {

    /**
     * 插入回复
     * 
     * @param postReply 回复实体
     * @return 影响行数
     */
    int insertReply(PostReply postReply);
    
    /**
     * 查询帖子回复列表
     * 
     * @param postId 帖子ID
     * @return 回复列表
     */
    java.util.List<com.baffang.user.vo.PostDetailResponseVO.ReplyVO> getPostReplies(@Param("postId") Integer postId);
}