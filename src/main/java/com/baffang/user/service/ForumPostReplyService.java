package com.baffang.user.service;

import com.baffang.common.entity.PostReply;

/**
 * 用户端帖子回复服务接口
 */
public interface ForumPostReplyService {
    
    /**
     * 回复帖子或回复
     * @param postReply 回复实体
     * @return 是否成功
     */
    Boolean replyToPostOrReply(PostReply postReply);
}