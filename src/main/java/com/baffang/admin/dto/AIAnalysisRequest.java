package com.baffang.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 分析请求
 */
@Data
@ApiModel("AI分析请求")
public class AIAnalysisRequest {

    @ApiModelProperty(value = "分析模块", example = "platform", 
            notes = "可选值: platform/station/vehicleAudit/complaint/auditRecord/forum/emergency/customerService/all")
    private String module;

    @ApiModelProperty(value = "自定义问题", example = "投诉处理效率怎么样？有什么可以改进的？")
    private String question;

    @ApiModelProperty(value = "是否包含原始数据", example = "false")
    private Boolean includeRawData = false;
}
