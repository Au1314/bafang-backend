package com.baffang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "AI决策详情VO")
public class AIDecisionDetailVO {
    
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "决策编号")
    private String decisionNo;
    
    @Schema(description = "决策类型")
    private Integer decisionType;
    
    @Schema(description = "决策类型名称")
    private String decisionTypeName;
    
    @Schema(description = "触发类型")
    private Integer triggerType;
    
    @Schema(description = "触发类型名称")
    private String triggerTypeName;
    
    @Schema(description = "业务类型")
    private String businessType;
    
    @Schema(description = "业务ID")
    private Integer businessId;
    
    @Schema(description = "输入数据")
    private Map<String, Object> inputData;
    
    @Schema(description = "AI分析结果（Markdown）")
    private String aiAnalysis;
    
    @Schema(description = "决策结果")
    private Map<String, Object> decisionResult;
    
    @Schema(description = "建议动作列表")
    private List<SuggestedActionVO> suggestedActions;
    
    @Schema(description = "置信度")
    private BigDecimal confidence;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "优先级名称")
    private String priorityName;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "是否自动执行")
    private Integer autoExecute;
    
    @Schema(description = "执行结果")
    private List<Map<String, Object>> executeResult;
    
    @Schema(description = "执行时间")
    private LocalDateTime executeTime;
    
    @Schema(description = "处理人ID")
    private Integer handledBy;
    
    @Schema(description = "处理人姓名")
    private String handlerName;
    
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
    
    @Schema(description = "处理备注")
    private String handleRemark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "动作执行日志")
    private List<ActionLogVO> actionLogs;
    
    @Data
    @Schema(description = "建议动作VO")
    public static class SuggestedActionVO {
        @Schema(description = "动作类型")
        private String actionType;
        
        @Schema(description = "动作名称")
        private String actionName;
        
        @Schema(description = "动作描述")
        private String description;
        
        @Schema(description = "动作参数")
        private Map<String, Object> params;
        
        @Schema(description = "优先级")
        private Integer priority;
        
        @Schema(description = "是否可自动执行")
        private Boolean autoExecutable;
    }
    
    @Data
    @Schema(description = "动作执行日志VO")
    public static class ActionLogVO {
        @Schema(description = "动作类型")
        private String actionType;
        
        @Schema(description = "动作名称")
        private String actionName;
        
        @Schema(description = "执行状态")
        private Integer executeStatus;
        
        @Schema(description = "执行状态名称")
        private String executeStatusName;
        
        @Schema(description = "执行结果")
        private String executeResult;
        
        @Schema(description = "错误信息")
        private String errorMsg;
        
        @Schema(description = "执行时间")
        private LocalDateTime executeTime;
        
        @Schema(description = "耗时（毫秒）")
        private Integer costMs;
    }
}
