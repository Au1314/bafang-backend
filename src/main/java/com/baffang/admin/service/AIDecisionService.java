package com.baffang.admin.service;

import com.baffang.admin.dto.AIDecisionQueryDTO;
import com.baffang.admin.dto.AIDecisionRequestDTO;
import com.baffang.admin.dto.DecisionHandleDTO;
import com.baffang.admin.vo.AIDecisionDetailVO;
import com.baffang.admin.vo.AIDecisionListVO;
import com.baffang.admin.vo.AIDecisionResponseVO;
import com.baffang.admin.vo.AIDecisionStatisticsVO;
import com.github.pagehelper.Page;

public interface AIDecisionService {

    // ==================== 查询方法 ====================
    
    Page<AIDecisionListVO> getDecisionList(AIDecisionQueryDTO query);
    
    AIDecisionDetailVO getDecisionDetail(Integer id);
    
    AIDecisionStatisticsVO getStatistics();

    // ==================== 决策请求 ====================
    
    AIDecisionResponseVO requestDecision(AIDecisionRequestDTO request);
    
    // 审核建议 - 默认手动触发
    AIDecisionResponseVO suggestAudit(Integer auditRecordId);
    
    // 审核建议 - 指定触发类型
    AIDecisionResponseVO suggestAudit(Integer auditRecordId, int triggerType);
    
    // 异常检测 - 默认手动触发
    AIDecisionResponseVO detectAnomalies();
    
    // 异常检测 - 指定触发类型
    AIDecisionResponseVO detectAnomalies(int triggerType);
    
    // 运营优化建议 - 默认手动触发
    AIDecisionResponseVO suggestOptimization();
    
    // 运营优化建议 - 指定触发类型
    AIDecisionResponseVO suggestOptimization(int triggerType);
    
    // 风险评估 - 默认手动触发
    AIDecisionResponseVO assessRisk(String businessType, Integer businessId);
    
    // 风险评估 - 指定触发类型
    AIDecisionResponseVO assessRisk(String businessType, Integer businessId, int triggerType);

    // ==================== 决策处理 ====================
    
    AIDecisionResponseVO handleDecision(DecisionHandleDTO dto, Integer adminId);
    
    void executeActions(Integer decisionId);
}
