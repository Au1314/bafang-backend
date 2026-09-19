package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "举报查询参数")
public class ForumReportQueryDTO {
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "审核状态：0-待审核，1-审核中，2-已通过，3-已拒绝")
    private Integer status;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "关键字")
    private String keyword;
    
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
