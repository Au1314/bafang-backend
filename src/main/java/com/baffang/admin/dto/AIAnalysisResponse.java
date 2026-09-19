package com.baffang.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 分析响应
 */
@Data
@Builder
@ApiModel("AI分析响应")
public class AIAnalysisResponse {

    @ApiModelProperty("是否成功")
    private boolean success;

    @ApiModelProperty("分析报告内容(Markdown格式)")
    private String analysis;

    @ApiModelProperty("错误信息")
    private String error;

    @ApiModelProperty("分析的模块")
    private String module;

    @ApiModelProperty("用户提问")
    private String question;

    @ApiModelProperty("原始统计数据")
    private Map<String, Object> rawData;

    @ApiModelProperty("分析时间")
    private LocalDateTime analysisTime;

    @ApiModelProperty("耗时(毫秒)")
    private Long costMs;
}
