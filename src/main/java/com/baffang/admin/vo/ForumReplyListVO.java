package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "评论列表")
public class ForumReplyListVO {
    
    @Schema(description = "评论ID")
    private Integer id;
    
    @Schema(description = "帖子ID")
    private Integer postId;
    
    @Schema(description = "帖子标题")
    private String postTitle;
    
    @Schema(description = "评论用户ID")
    private Integer userId;
    
    @Schema(description = "评论用户名")
    private String userName;
    
    @Schema(description = "评论用户头像")
    private String userAvatar;
    
    @Schema(description = "父评论ID")
    private Integer parentId;
    
    @Schema(description = "被回复用户ID")
    private Integer replyToUserId;
    
    @Schema(description = "被回复用户名")
    private String replyToUserName;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
