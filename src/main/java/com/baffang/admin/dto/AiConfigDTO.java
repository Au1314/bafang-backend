// ==================== AI配置DTO ====================
package com.baffang.admin.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiConfigDTO {
    
    /** AI客服开关 */
    private Boolean aiEnabled;
    
    /** AI模型名称 */
    private String aiModel;
    
    /** AI服务API密钥 */
    private String aiApiKey;
    
    /** AI服务API地址 */
    private String aiApiUrl;
    
    /** AI系统提示词 */
    private String aiSystemPrompt;
    
    /** AI回复最大token数 */
    private Integer aiMaxTokens;
    
    /** AI回复温度参数 */
    private Double aiTemperature;
    
    /** AI欢迎语 */
    private String aiWelcomeMessage;
    
    /** 转人工关键词 */
    private List<String> transferKeywords;
    
    /** 连续无法解答次数阈值 */
    private Integer autoTransferThreshold;
    
    /** 人工客服工作开始时间 */
    private String workingHoursStart;
    
    /** 人工客服工作结束时间 */
    private String workingHoursEnd;
    
    /** 非工作时间提示语 */
    private String offlineMessage;
}
