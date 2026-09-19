package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "帖子查询参数")
public class ForumPostQueryDTO {
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "关键字（标题、内容）")
    private String keyword;
    
    @Schema(description = "作者ID")
    private Integer userId;
    
    @Schema(description = "是否置顶：0-否，1-是")
    private Integer isTop;
    
    @Schema(description = "是否精华：0-否，1-是")
    private Integer isEssence;
    
    @Schema(description = "开始时间")
    private String startTime;
    
    @Schema(description = "结束时间")
    private String endTime;


    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
    }
}

