package com.baffang.user.vo;

import com.baffang.common.entity.ForumPost;
import com.baffang.common.entity.PostReply;
import lombok.Data;

import java.util.List;

/**
 * 帖子详情VO
 */
@Data
public class PostDetailVO {

    /**
     * 帖子信息
     */
    private ForumPost post;

    /**
     * 回复列表
     */
    private List<PostReply> replies;
}
