package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "决策处理参数")
public class DecisionHandleDTO {
    
    @NotNull(message = "决策记录ID不能为空")
    @Schema(description = "决策记录ID")
    private Integer decisionId;
    
    @NotBlank(message = "处理动作不能为空")
    @Schema(description = "处理动作：adopt-采纳，ignore-忽略，partial-部分采纳")
    private String action;
    
    @Schema(description = "要执行的动作索引（部分采纳时使用）")
    private List<Integer> actionIndexes;
    
    @Schema(description = "处理备注")
    private String remark;
}
