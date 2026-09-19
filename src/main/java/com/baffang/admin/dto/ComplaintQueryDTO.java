package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "投诉/故障查询条件")
public class ComplaintQueryDTO {
    
    @Schema(description = "页码")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数")
    private Integer pageSize = 10;
    
    @Schema(description = "审核类型：2-投诉，3-故障，不传查全部")
    private Integer auditType;
    
    @Schema(description = "状态：0-待审核，1-审核中，2-已通过，3-已拒绝，4-需补充材料")
    private Integer status;
    
    @Schema(description = "优先级：0-低，1-普通，2-高，3-紧急")
    private Integer priority;
    
    @Schema(description = "搜索关键词（标题/审核单号）")
    private String keyword;
    
    @Schema(description = "充电桩ID")
    private Integer stationId;
    
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
