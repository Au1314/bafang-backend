package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "AI决策响应VO")
public class AIDecisionResponseVO {
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "错误信息")
    private String error;
    
    @Schema(description = "决策记录ID")
    private Integer decisionId;
    
    @Schema(description = "决策编号")
    private String decisionNo;
    
    @Schema(description = "AI分析结果（Markdown格式）")
    private String analysis;
    
    @Schema(description = "建议动作列表")
    private List<AIDecisionDetailVO.SuggestedActionVO> suggestedActions;
    
    @Schema(description = "置信度")
    private Double confidence;
    
    @Schema(description = "是否会自动执行")
    private Boolean willAutoExecute;
    
    @Schema(description = "处理耗时（毫秒）")
    private Long costMs;
}
