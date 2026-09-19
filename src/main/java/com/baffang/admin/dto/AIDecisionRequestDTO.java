package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "AI决策请求参数")
public class AIDecisionRequestDTO {
    
    @NotNull(message = "决策类型不能为空")
    @Schema(description = "决策类型：0-审核建议，1-异常预警，2-资源调度，3-风险控制，4-运营优化")
    private Integer decisionType;
    
    @Schema(description = "业务类型")
    private String businessType;
    
    @Schema(description = "业务ID")
    private Integer businessId;
    
    @Schema(description = "附加问题/要求")
    private String additionalQuestion;
    
    @Schema(description = "是否自动执行（如果AI置信度达标）")
    private Boolean autoExecute;
}
