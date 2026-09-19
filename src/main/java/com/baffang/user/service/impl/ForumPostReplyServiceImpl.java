package com.baffang.user.service.impl;

import com.baffang.common.entity.PostReply;
import com.baffang.user.mapper.UserForumPostReplyMapper;
import com.baffang.user.service.ForumPostReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户端帖子回复服务实现类
 */
@Service
public class ForumPostReplyServiceImpl implements ForumPostReplyService {
    
    @Autowired
    private UserForumPostReplyMapper userForumPostReplyMapper;
    
    /**
     * 回复帖子或回复
     * @param postReply 回复实体
     * @return 是否成功
     */
    @Override
    public Boolean replyToPostOrReply(PostReply postReply) {
        int result = userForumPostReplyMapper.insertReply(postReply);
        return result > 0;
    }
}