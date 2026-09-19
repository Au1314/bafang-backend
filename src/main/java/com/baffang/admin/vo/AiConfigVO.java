// ==================== AI配置VO ====================
package com.baffang.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class AiConfigVO {
    
    private Boolean aiEnabled;
    private String aiModel;
    private String aiApiKey;  // 脱敏显示
    private String aiApiUrl;
    private String aiSystemPrompt;
    private Integer aiMaxTokens;
    private Double aiTemperature;
    private String aiWelcomeMessage;
    private List<String> transferKeywords;
    private Integer autoTransferThreshold;
    private String workingHoursStart;
    private String workingHoursEnd;
    private String offlineMessage;
}
