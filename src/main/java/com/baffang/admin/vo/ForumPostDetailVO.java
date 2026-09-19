package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "帖子详情")
public class ForumPostDetailVO {
    
    @Schema(description = "帖子ID")
    private Integer id;
    
    @Schema(description = "作者ID")
    private Integer userId;
    
    @Schema(description = "作者用户名")
    private String userName;
    
    @Schema(description = "作者昵称")
    private String userNickname;
    
    @Schema(description = "作者头像")
    private String userAvatar;
    
    @Schema(description = "作者手机号（脱敏）")
    private String userPhone;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "收藏数")
    private Integer collectCount;
    
    @Schema(description = "评论数")
    private Integer replyCount;
    
    @Schema(description = "是否置顶")
    private Integer isTop;
    
    @Schema(description = "是否精华")
    private Integer isEssence;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @Schema(description = "最新评论列表（前5条）")
    private List<ForumReplyListVO> latestReplies;

    // ========== 新增字段 ==========
    /**
     * 图片列表
     */
    @Schema(description = "图片列表")
    private List<String> images;
}
