package com.baffang.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 帖子详情响应VO
 */
@Data
public class PostDetailResponseVO {

    /**
     * 帖子信息
     */
    private PostVO post;

    /**
     * 回复列表
     */
    private List<ReplyVO> replies;

    /**
     * 当前用户是否已点赞该帖子
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏该帖子
     */
    private Boolean isCollected;

    /**
     * 帖子VO
     */
    @Data
    public static class PostVO {
        private Integer postId;
        private Integer userId;
        private String title;
        private String content;
        private List<String> images;
        private String createTime;
        private Integer likeCount;
        private Integer collectCount;
        private String publisherName;
        private String publisherAvatar;
    }

    /**
     * 回复VO
     */
    @Data
    public static class ReplyVO {
        private Integer replyId;
        private Integer userId;
        private Integer parentId;
        private Integer targetUserId;
        private String content;
        private Integer likeCount;
        private String createTime;
        private String userName;
        private String userAvatar;
    }
}
