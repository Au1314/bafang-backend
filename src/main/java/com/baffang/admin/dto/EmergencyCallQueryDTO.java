package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "紧急呼叫查询参数")
public class EmergencyCallQueryDTO {
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "关键字（呼叫位置、用户名）")
    private String keyword;
    
    @Schema(description = "呼叫类型：0-拖车，1-充电桩，2-其他")
    private Integer type;
    
    @Schema(description = "状态：0-待响应，1-已响应，2-已完成，3-已取消")
    private Integer status;
    
    @Schema(description = "响应管理员ID")
    private Integer responseAdminId;
    
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
