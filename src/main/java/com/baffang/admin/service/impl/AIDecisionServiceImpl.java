package com.baffang.admin.service.impl;

import com.baffang.common.constant.AIDecisionTriggerType;
import com.baffang.admin.dto.AIDecisionQueryDTO;
import com.baffang.admin.dto.AIDecisionRequestDTO;
import com.baffang.admin.dto.DecisionHandleDTO;
import com.baffang.admin.mapper.AIDecisionActionLogMapper;
import com.baffang.admin.mapper.AIDecisionRecordMapper;
import com.baffang.admin.mapper.AIDecisionRuleMapper;
import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.mapper.StatsMapper;
import com.baffang.admin.service.AIDecisionService;
import com.baffang.admin.vo.AIDecisionDetailVO;
import com.baffang.admin.vo.AIDecisionListVO;
import com.baffang.admin.vo.AIDecisionResponseVO;
import com.baffang.admin.vo.AIDecisionStatisticsVO;
import com.baffang.common.entity.AIDecisionActionLog;
import com.baffang.common.entity.AIDecisionRecord;
import com.baffang.common.entity.AIDecisionRule;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.enums.DecisionStatusEnum;
import com.baffang.common.enums.DecisionTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.util.DeepSeekClient;
import com.github.pagehelper.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AIDecisionServiceImpl implements AIDecisionService {

    private final AIDecisionRecordMapper decisionRecordMapper;
    private final AIDecisionActionLogMapper actionLogMapper;
    private final AIDecisionRuleMapper ruleMapper;
    private final StatsMapper statsMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final DeepSeekClient deepSeekClient;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    // ==================== 查询方法 ====================

    @Override
    public Page<AIDecisionListVO> getDecisionList(AIDecisionQueryDTO query) {
        int pageNum = Optional.ofNullable(query.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(query.getPageSize()).orElse(10);
        int offset = (pageNum - 1) * pageSize;

        List<AIDecisionListVO> list = decisionRecordMapper.selectPageList(query, offset, pageSize);
        Long total = decisionRecordMapper.countByCondition(query);

        // 填充名称
        list.forEach(this::fillListNames);

        Page<AIDecisionListVO> page = new Page<>(pageNum, pageSize);
        page.addAll(list);
        page.setTotal(total);
        return page;
    }

    @Override
    public AIDecisionDetailVO getDecisionDetail(Integer id) {
        AIDecisionRecord record = decisionRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("决策记录不存在");
        }
        return convertToDetailVO(record);
    }

    @Override
    public AIDecisionStatisticsVO getStatistics() {
        AIDecisionStatisticsVO stats = new AIDecisionStatisticsVO();

        List<Map<String, Object>> byStatus = decisionRecordMapper.countByStatus();
        int total = 0, pending = 0, adopted = 0, ignored = 0, executed = 0, failed = 0;
        for (Map<String, Object> item : byStatus) {
            int status = ((Number) item.get("status")).intValue();
            int count = ((Number) item.get("count")).intValue();
            total += count;
            switch (status) {
                case 0: pending = count; break;
                case 1: adopted = count; break;
                case 2: ignored = count; break;
                case 5: executed = count; break;
                case 6: failed = count; break;
            }
        }
        stats.setTotalCount(total);
        stats.setPendingCount(pending);
        stats.setAdoptedCount(adopted);
        stats.setIgnoredCount(ignored);
        stats.setExecutedCount(executed);
        stats.setFailedCount(failed);

        stats.setTodayCount(Optional.ofNullable(decisionRecordMapper.countToday()).orElse(0));
        stats.setAvgConfidence(Optional.ofNullable(decisionRecordMapper.avgConfidence()).orElse(0.0));
        stats.setByType(decisionRecordMapper.countByType());
        stats.setByPriority(decisionRecordMapper.countByPriority());

        return stats;
    }

    // ==================== 决策请求 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO requestDecision(AIDecisionRequestDTO request) {
        long startTime = System.currentTimeMillis();

        try {
            DecisionTypeEnum decisionType = DecisionTypeEnum.fromCode(request.getDecisionType());
            if (decisionType == null) {
                return AIDecisionResponseVO.builder()
                        .success(false)
                        .error("无效的决策类型")
                        .build();
            }

            // 通过 requestDecision 调用的都是手动触发
            switch (decisionType) {
                case AUDIT_SUGGESTION:
                    return suggestAudit(request.getBusinessId(), AIDecisionTriggerType.MANUAL);
                case ANOMALY_WARNING:
                    return detectAnomalies(AIDecisionTriggerType.MANUAL);
                case OPERATION_OPTIMIZE:
                    return suggestOptimization(AIDecisionTriggerType.MANUAL);
                case RISK_CONTROL:
                    return assessRisk(request.getBusinessType(), request.getBusinessId(), AIDecisionTriggerType.MANUAL);
                default:
                    return AIDecisionResponseVO.builder()
                            .success(false)
                            .error("暂不支持该决策类型")
                            .build();
            }
        } catch (Exception e) {
            log.error("AI决策请求失败", e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("决策失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 审核建议 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO suggestAudit(Integer auditRecordId) {
        return suggestAudit(auditRecordId, AIDecisionTriggerType.MANUAL);
    }

    @Override
    @Transactional
    public AIDecisionResponseVO suggestAudit(Integer auditRecordId, int triggerType) {
        long startTime = System.currentTimeMillis();

        try {
            if (auditRecordId == null) {
                return AIDecisionResponseVO.builder()
                        .success(false)
                        .error("审核记录ID不能为空")
                        .build();
            }

            AuditRecord auditRecord = auditRecordMapper.selectById(auditRecordId);
            if (auditRecord == null) {
                return AIDecisionResponseVO.builder()
                        .success(false)
                        .error("审核记录不存在")
                        .build();
            }

            // 构建输入数据
            Map<String, Object> inputData = new LinkedHashMap<>();
            inputData.put("auditId", auditRecord.getId());
            inputData.put("auditNo", auditRecord.getAuditNo());
            inputData.put("auditType", auditRecord.getAuditType());
            inputData.put("auditTypeName", getAuditTypeName(auditRecord.getAuditType()));
            inputData.put("title", auditRecord.getTitle());
            inputData.put("content", auditRecord.getContent());
            inputData.put("currentStatus", auditRecord.getCurrentStatus());

            // 调用 AI 分析
            String systemPrompt = buildAuditSystemPrompt();
            String userPrompt = buildAuditUserPrompt(inputData);
            String aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 解析响应
            List<AIDecisionDetailVO.SuggestedActionVO> actions = parseActionsFromResponse(aiResponse);
            double confidence = extractConfidence(aiResponse);
            int priority = extractPriority(aiResponse);

            // 创建决策记录 - 使用传入的 triggerType
            AIDecisionRecord record = createDecisionRecord(
                    DecisionTypeEnum.AUDIT_SUGGESTION,
                    "AUDIT",
                    auditRecordId,
                    inputData,
                    aiResponse,
                    actions,
                    confidence,
                    priority,
                    triggerType
            );

            // 检查是否自动执行
            AIDecisionRule rule = ruleMapper.selectByRuleCode("AUDIT_SUGGESTION");
            boolean willAutoExecute = checkAutoExecute(rule, confidence);

            if (willAutoExecute) {
                record.setAutoExecute(1);
                record.setStatus(DecisionStatusEnum.EXECUTING.getCode());
                decisionRecordMapper.updateById(record);
                executeActionsAsync(record.getId());
            }

            return AIDecisionResponseVO.builder()
                    .success(true)
                    .decisionId(record.getId())
                    .decisionNo(record.getDecisionNo())
                    .analysis(aiResponse)
                    .suggestedActions(actions)
                    .confidence(confidence)
                    .willAutoExecute(willAutoExecute)
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("审核建议生成失败: auditRecordId={}, triggerType={}", auditRecordId, triggerType, e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("生成审核建议失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 异常检测 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO detectAnomalies() {
        return detectAnomalies(AIDecisionTriggerType.MANUAL);
    }

    @Override
    @Transactional
    public AIDecisionResponseVO detectAnomalies(int triggerType) {
        long startTime = System.currentTimeMillis();

        try {
            // 收集异常检测数据
            Map<String, Object> inputData = collectAnomalyData();

            // 调用 AI 检测
            String systemPrompt = buildAnomalySystemPrompt();
            String userPrompt = buildAnomalyUserPrompt(inputData);
            String aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 解析响应
            List<AIDecisionDetailVO.SuggestedActionVO> actions = parseActionsFromResponse(aiResponse);
            double confidence = extractConfidence(aiResponse);
            int priority = extractPriority(aiResponse);

            // 创建决策记录 - 使用传入的 triggerType
            AIDecisionRecord record = createDecisionRecord(
                    DecisionTypeEnum.ANOMALY_WARNING,
                    null,
                    null,
                    inputData,
                    aiResponse,
                    actions,
                    confidence,
                    priority,
                    triggerType
            );

            // 高优先级异常通知
            if (priority >= 2 && !actions.isEmpty()) {
                log.warn("【异常预警】优先级:{}, 决策编号:{}, 触发类型:{}", 
                        priority, record.getDecisionNo(), getTriggerTypeName(triggerType));
            }

            return AIDecisionResponseVO.builder()
                    .success(true)
                    .decisionId(record.getId())
                    .decisionNo(record.getDecisionNo())
                    .analysis(aiResponse)
                    .suggestedActions(actions)
                    .confidence(confidence)
                    .willAutoExecute(false)
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("异常检测失败, triggerType={}", triggerType, e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("异常检测失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 运营优化建议 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO suggestOptimization() {
        return suggestOptimization(AIDecisionTriggerType.MANUAL);
    }

    @Override
    @Transactional
    public AIDecisionResponseVO suggestOptimization(int triggerType) {
        long startTime = System.currentTimeMillis();

        try {
            // 收集运营数据
            Map<String, Object> inputData = collectOperationData();

            // 调用 AI
            String systemPrompt = buildOptimizationSystemPrompt();
            String userPrompt = buildOptimizationUserPrompt(inputData);
            String aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 解析响应
            List<AIDecisionDetailVO.SuggestedActionVO> actions = parseActionsFromResponse(aiResponse);
            double confidence = extractConfidence(aiResponse);

            // 创建决策记录 - 使用传入的 triggerType
            AIDecisionRecord record = createDecisionRecord(
                    DecisionTypeEnum.OPERATION_OPTIMIZE,
                    null,
                    null,
                    inputData,
                    aiResponse,
                    actions,
                    confidence,
                    1,
                    triggerType
            );

            return AIDecisionResponseVO.builder()
                    .success(true)
                    .decisionId(record.getId())
                    .decisionNo(record.getDecisionNo())
                    .analysis(aiResponse)
                    .suggestedActions(actions)
                    .confidence(confidence)
                    .willAutoExecute(false)
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("运营优化建议生成失败, triggerType={}", triggerType, e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("生成优化建议失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 风险评估 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO assessRisk(String businessType, Integer businessId) {
        return assessRisk(businessType, businessId, AIDecisionTriggerType.MANUAL);
    }

    @Override
    @Transactional
    public AIDecisionResponseVO assessRisk(String businessType, Integer businessId, int triggerType) {
        long startTime = System.currentTimeMillis();

        try {
            // 收集风险数据
            Map<String, Object> inputData = new LinkedHashMap<>();
            inputData.put("businessType", businessType);
            inputData.put("businessId", businessId);
            inputData.put("collectTime", LocalDateTime.now().toString());

            // 调用 AI
            String systemPrompt = buildRiskSystemPrompt();
            String userPrompt = buildRiskUserPrompt(inputData);
            String aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 解析响应
            List<AIDecisionDetailVO.SuggestedActionVO> actions = parseActionsFromResponse(aiResponse);
            double confidence = extractConfidence(aiResponse);
            int priority = extractPriority(aiResponse);

            // 创建决策记录 - 使用传入的 triggerType
            AIDecisionRecord record = createDecisionRecord(
                    DecisionTypeEnum.RISK_CONTROL,
                    businessType,
                    businessId,
                    inputData,
                    aiResponse,
                    actions,
                    confidence,
                    priority,
                    triggerType
            );

            // 高风险自动执行
            AIDecisionRule rule = ruleMapper.selectByRuleCode("RISK_CONTROL");
            boolean willAutoExecute = priority >= 2 && checkAutoExecute(rule, confidence);

            if (willAutoExecute) {
                record.setAutoExecute(1);
                record.setStatus(DecisionStatusEnum.EXECUTING.getCode());
                decisionRecordMapper.updateById(record);
                executeActionsAsync(record.getId());
            }

            return AIDecisionResponseVO.builder()
                    .success(true)
                    .decisionId(record.getId())
                    .decisionNo(record.getDecisionNo())
                    .analysis(aiResponse)
                    .suggestedActions(actions)
                    .confidence(confidence)
                    .willAutoExecute(willAutoExecute)
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("风险评估失败, triggerType={}", triggerType, e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("风险评估失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 决策处理 ====================

    @Override
    @Transactional
    public AIDecisionResponseVO handleDecision(DecisionHandleDTO dto, Integer adminId) {
        long startTime = System.currentTimeMillis();

        try {
            AIDecisionRecord record = decisionRecordMapper.selectById(dto.getDecisionId());
            if (record == null) {
                return AIDecisionResponseVO.builder()
                        .success(false)
                        .error("决策记录不存在")
                        .build();
            }

            if (record.getStatus() != DecisionStatusEnum.PENDING.getCode()) {
                return AIDecisionResponseVO.builder()
                        .success(false)
                        .error("该决策已被处理")
                        .build();
            }

            switch (dto.getAction()) {
                case "adopt":
                    record.setStatus(DecisionStatusEnum.ADOPTED.getCode());
                    record.setAutoExecute(1);
                    decisionRecordMapper.updateById(record);
                    executeActions(record.getId());
                    break;

                case "ignore":
                    record.setStatus(DecisionStatusEnum.IGNORED.getCode());
                    break;

                case "partial":
                    record.setStatus(DecisionStatusEnum.PARTIAL_ADOPTED.getCode());
                    executePartialActions(record.getId(), dto.getActionIndexes());
                    break;

                default:
                    return AIDecisionResponseVO.builder()
                            .success(false)
                            .error("无效的处理动作")
                            .build();
            }

            record.setHandledBy(adminId);
            record.setHandleTime(LocalDateTime.now());
            record.setHandleRemark(dto.getRemark());
            decisionRecordMapper.updateById(record);

            return AIDecisionResponseVO.builder()
                    .success(true)
                    .decisionId(record.getId())
                    .decisionNo(record.getDecisionNo())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("处理决策失败: decisionId={}", dto.getDecisionId(), e);
            return AIDecisionResponseVO.builder()
                    .success(false)
                    .error("处理决策失败: " + e.getMessage())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    @Transactional
    public void executeActions(Integer decisionId) {
        AIDecisionRecord record = decisionRecordMapper.selectById(decisionId);
        if (record == null || record.getSuggestedActions() == null) {
            return;
        }

        try {
            List<Map<String, Object>> actions = gson.fromJson(
                    record.getSuggestedActions(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType()
            );

            List<Map<String, Object>> results = new ArrayList<>();
            boolean allSuccess = true;

            for (int i = 0; i < actions.size(); i++) {
                Map<String, Object> action = actions.get(i);
                Map<String, Object> result = executeSingleAction(decisionId, action, i);
                results.add(result);
                if (!"SUCCESS".equals(result.get("status"))) {
                    allSuccess = false;
                }
            }

            record.setExecuteResult(gson.toJson(results));
            record.setExecuteTime(LocalDateTime.now());
            record.setStatus(allSuccess ?
                    DecisionStatusEnum.EXECUTED.getCode() :
                    DecisionStatusEnum.EXECUTE_FAILED.getCode());
            decisionRecordMapper.updateById(record);

        } catch (Exception e) {
            log.error("执行决策动作失败: decisionId={}", decisionId, e);
            record.setStatus(DecisionStatusEnum.EXECUTE_FAILED.getCode());
            record.setExecuteResult(gson.toJson(Map.of("error", e.getMessage())));
            decisionRecordMapper.updateById(record);
        }
    }

    // ==================== 私有辅助方法 ====================

    private void fillListNames(AIDecisionListVO vo) {
        vo.setDecisionTypeName(DecisionTypeEnum.getDesc(vo.getDecisionType()));
        vo.setStatusName(DecisionStatusEnum.getDesc(vo.getStatus()));
        vo.setPriorityName(getPriorityName(vo.getPriority()));
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "普通";
        switch (priority) {
            case 0: return "低";
            case 1: return "普通";
            case 2: return "高";
            case 3: return "紧急";
            default: return "普通";
        }
    }

    private String getTriggerTypeName(Integer triggerType) {
        if (triggerType == null) return "未知";
        switch (triggerType) {
            case 0: return "定时任务";
            case 1: return "事件触发";
            case 2: return "手动触发";
            default: return "未知";
        }
    }

    private String getExecuteStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待执行";
            case 1: return "执行中";
            case 2: return "成功";
            case 3: return "失败";
            case 4: return "跳过";
            default: return "未知";
        }
    }

    private AIDecisionDetailVO convertToDetailVO(AIDecisionRecord record) {
        AIDecisionDetailVO vo = new AIDecisionDetailVO();
        vo.setId(record.getId());
        vo.setDecisionNo(record.getDecisionNo());
        vo.setDecisionType(record.getDecisionType());
        vo.setDecisionTypeName(DecisionTypeEnum.getDesc(record.getDecisionType()));
        vo.setTriggerType(record.getTriggerType());
        vo.setTriggerTypeName(getTriggerTypeName(record.getTriggerType()));
        vo.setBusinessType(record.getBusinessType());
        vo.setBusinessId(record.getBusinessId());
        vo.setAiAnalysis(record.getAiAnalysis());
        vo.setConfidence(record.getConfidence());
        vo.setPriority(record.getPriority());
        vo.setPriorityName(getPriorityName(record.getPriority()));
        vo.setStatus(record.getStatus());
        vo.setStatusName(DecisionStatusEnum.getDesc(record.getStatus()));
        vo.setAutoExecute(record.getAutoExecute());
        vo.setExecuteTime(record.getExecuteTime());
        vo.setHandledBy(record.getHandledBy());
        vo.setHandleTime(record.getHandleTime());
        vo.setHandleRemark(record.getHandleRemark());
        vo.setCreateTime(record.getCreateTime());

        // 解析JSON字段
        if (record.getInputData() != null) {
            vo.setInputData(gson.fromJson(record.getInputData(), new TypeToken<Map<String, Object>>(){}.getType()));
        }
        if (record.getDecisionResult() != null) {
            vo.setDecisionResult(gson.fromJson(record.getDecisionResult(), new TypeToken<Map<String, Object>>(){}.getType()));
        }
        if (record.getSuggestedActions() != null) {
            vo.setSuggestedActions(gson.fromJson(record.getSuggestedActions(), new TypeToken<List<AIDecisionDetailVO.SuggestedActionVO>>(){}.getType()));
        }
        if (record.getExecuteResult() != null) {
            vo.setExecuteResult(gson.fromJson(record.getExecuteResult(), new TypeToken<List<Map<String, Object>>>(){}.getType()));
        }

        // 查询执行日志
        List<AIDecisionActionLog> logs = actionLogMapper.selectByDecisionId(record.getId());
        List<AIDecisionDetailVO.ActionLogVO> logVOs = new ArrayList<>();
        for (AIDecisionActionLog logItem : logs) {
            AIDecisionDetailVO.ActionLogVO logVO = new AIDecisionDetailVO.ActionLogVO();
            logVO.setActionType(logItem.getActionType());
            logVO.setActionName(logItem.getActionName());
            logVO.setExecuteStatus(logItem.getExecuteStatus());
            logVO.setExecuteStatusName(getExecuteStatusName(logItem.getExecuteStatus()));
            logVO.setExecuteResult(logItem.getExecuteResult());
            logVO.setErrorMsg(logItem.getErrorMsg());
            logVO.setExecuteTime(logItem.getExecuteTime());
            logVO.setCostMs(logItem.getCostMs());
            logVOs.add(logVO);
        }
        vo.setActionLogs(logVOs);

        return vo;
    }

    /**
     * 创建决策记录
     * @param triggerType 触发类型：0=定时任务 1=事件触发 2=手动触发
     */
    private AIDecisionRecord createDecisionRecord(
            DecisionTypeEnum type,
            String businessType,
            Integer businessId,
            Map<String, Object> inputData,
            String aiAnalysis,
            List<AIDecisionDetailVO.SuggestedActionVO> actions,
            double confidence,
            int priority,
            int triggerType) {

        AIDecisionRecord record = new AIDecisionRecord();
        record.setDecisionNo(generateDecisionNo(type));
        record.setDecisionType(type.getCode());
        record.setTriggerType(triggerType);  // 使用传入的触发类型
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        record.setInputData(gson.toJson(inputData));
        record.setAiAnalysis(aiAnalysis);
        record.setSuggestedActions(gson.toJson(actions));
        record.setConfidence(BigDecimal.valueOf(confidence));
        record.setPriority(priority);
        record.setStatus(DecisionStatusEnum.PENDING.getCode());
        record.setAutoExecute(0);

        decisionRecordMapper.insert(record);
        return record;
    }

    private String generateDecisionNo(DecisionTypeEnum type) {
        String prefix = "DEC";
        switch (type) {
            case AUDIT_SUGGESTION: prefix = "AUD"; break;
            case ANOMALY_WARNING: prefix = "ANO"; break;
            case RESOURCE_SCHEDULE: prefix = "RES"; break;
            case RISK_CONTROL: prefix = "RSK"; break;
            case OPERATION_OPTIMIZE: prefix = "OPT"; break;
        }
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    private boolean checkAutoExecute(AIDecisionRule rule, double confidence) {
        return rule != null
                && rule.getAutoExecute() == 1
                && confidence >= rule.getExecuteThreshold().doubleValue();
    }

    private void executeActionsAsync(Integer decisionId) {
        new Thread(() -> executeActions(decisionId)).start();
    }

    private void executePartialActions(Integer decisionId, List<Integer> actionIndexes) {
        AIDecisionRecord record = decisionRecordMapper.selectById(decisionId);
        if (record == null || record.getSuggestedActions() == null || actionIndexes == null) {
            return;
        }

        try {
            List<Map<String, Object>> actions = gson.fromJson(
                    record.getSuggestedActions(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType()
            );

            List<Map<String, Object>> results = new ArrayList<>();

            for (Integer index : actionIndexes) {
                if (index >= 0 && index < actions.size()) {
                    Map<String, Object> action = actions.get(index);
                    Map<String, Object> result = executeSingleAction(decisionId, action, index);
                    results.add(result);
                }
            }

            record.setExecuteResult(gson.toJson(results));
            record.setExecuteTime(LocalDateTime.now());
            decisionRecordMapper.updateById(record);

        } catch (Exception e) {
            log.error("执行部分决策动作失败: decisionId={}", decisionId, e);
        }
    }

    private Map<String, Object> executeSingleAction(Integer decisionId, Map<String, Object> action, int index) {
        long startTime = System.currentTimeMillis();
        String actionType = (String) action.get("actionType");
        String actionName = (String) action.get("actionName");

        AIDecisionActionLog actionLog = new AIDecisionActionLog();
        actionLog.setDecisionId(decisionId);
        actionLog.setActionType(actionType);
        actionLog.setActionName(actionName);
        actionLog.setActionParams(gson.toJson(action.get("params")));
        actionLog.setExecuteStatus(1);
        actionLogMapper.insert(actionLog);

        Map<String, Object> result = new HashMap<>();
        result.put("index", index);
        result.put("actionType", actionType);
        result.put("actionName", actionName);

        try {
            Object executeResult = executeActionByType(actionType, action);
            result.put("status", "SUCCESS");
            result.put("result", executeResult);
            actionLog.setExecuteStatus(2);
            actionLog.setExecuteResult(gson.toJson(executeResult));
        } catch (Exception e) {
            log.error("执行动作失败: actionType={}", actionType, e);
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            actionLog.setExecuteStatus(3);
            actionLog.setErrorMsg(e.getMessage());
        }

        actionLog.setExecuteTime(LocalDateTime.now());
        actionLog.setCostMs((int) (System.currentTimeMillis() - startTime));
        actionLogMapper.updateById(actionLog);

        return result;
    }

    @SuppressWarnings("unchecked")
    private Object executeActionByType(String actionType, Map<String, Object> action) {
        Map<String, Object> params = (Map<String, Object>) action.get("params");

        switch (actionType != null ? actionType : "") {
            case "APPROVE_AUDIT":
                return executeApproveAudit(params);
            case "REJECT_AUDIT":
                return executeRejectAudit(params);
            case "SEND_NOTIFICATION":
                log.info("发送通知: {}", params);
                return Map.of("message", "通知已发送");
            case "CREATE_ALERT":
                log.info("创建预警: {}", params);
                return Map.of("message", "预警已创建");
            default:
                return Map.of("message", "动作类型暂不支持: " + actionType);
        }
    }

    private Object executeApproveAudit(Map<String, Object> params) {
        if (params == null || params.get("auditId") == null) {
            return Map.of("message", "参数缺失");
        }
        Integer auditId = ((Number) params.get("auditId")).intValue();
        String opinion = (String) params.getOrDefault("opinion", "AI自动审核通过");

        log.info("====== executeApproveAudit ======");
        log.info("auditId: {}, opinion: {}", auditId, opinion);

        AuditRecord auditRecord = auditRecordMapper.selectById(auditId);

        if (auditRecord == null) {
            return Map.of("auditId", auditId, "message", "审核记录不存在");
        }

        log.info("当前审核状态: {}", auditRecord.getCurrentStatus());

        Integer currentStatus = auditRecord.getCurrentStatus();
        if (currentStatus == null || (currentStatus != 0 && currentStatus != 1)) {
            return Map.of("auditId", auditId, "currentStatus", currentStatus,
                    "message", "只有待审核(0)或审核中(1)的记录才能处理");
        }

        auditRecord.setCurrentStatus(2);  // 已通过
        auditRecord.setFinalOpinion(opinion);
        auditRecord.setFinishTime(new Date());

        int rows = auditRecordMapper.updateById(auditRecord);
        log.info("更新行数: {}", rows);

        return rows > 0
                ? Map.of("auditId", auditId, "newStatus", 2, "message", "审核已通过")
                : Map.of("auditId", auditId, "message", "更新失败");
    }

    private Object executeRejectAudit(Map<String, Object> params) {
        if (params == null || params.get("auditId") == null) {
            return Map.of("message", "参数缺失");
        }
        Integer auditId = ((Number) params.get("auditId")).intValue();
        String reason = (String) params.getOrDefault("reason", "AI审核未通过");

        AuditRecord auditRecord = auditRecordMapper.selectById(auditId);

        if (auditRecord == null) {
            return Map.of("auditId", auditId, "message", "审核记录不存在");
        }

        Integer currentStatus = auditRecord.getCurrentStatus();
        if (currentStatus == null || (currentStatus != 0 && currentStatus != 1)) {
            return Map.of("auditId", auditId, "currentStatus", currentStatus,
                    "message", "只有待审核(0)或审核中(1)的记录才能处理");
        }

        auditRecord.setCurrentStatus(3);  // 已拒绝
        auditRecord.setRejectReason(reason);
        auditRecord.setFinishTime(new Date());

        int rows = auditRecordMapper.updateById(auditRecord);

        return rows > 0
                ? Map.of("auditId", auditId, "newStatus", 3, "message", "审核已拒绝")
                : Map.of("auditId", auditId, "message", "更新失败");
    }

    // ==================== 数据收集 ====================

    private Map<String, Object> collectAnomalyData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", safeQuery(() -> statsMapper.countTotalUsers(), 0));
        data.put("todayNewUsers", safeQuery(() -> statsMapper.countTodayNewUsers(), 0));
        data.put("totalOrders", safeQuery(() -> statsMapper.countTotalOrders(), 0));
        data.put("todayOrders", safeQuery(() -> statsMapper.countTodayOrders(), 0));
        data.put("todayRevenue", safeQuery(() -> statsMapper.sumTodayRevenue(), 0.0));
        data.put("pendingAuditCount", safeQuery(() -> statsMapper.countPendingAuditRecords(), 0));
        data.put("todayEmergencyCalls", safeQuery(() -> statsMapper.countTodayEmergencyCalls(), 0));
        data.put("todayComplaints", safeQuery(() -> statsMapper.countTodayNewComplaints(), 0));
        data.put("collectTime", LocalDateTime.now().toString());
        return data;
    }

    private Map<String, Object> collectOperationData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", safeQuery(() -> statsMapper.countTotalUsers(), 0));
        data.put("totalOrders", safeQuery(() -> statsMapper.countTotalOrders(), 0));
        data.put("totalRevenue", safeQuery(() -> statsMapper.sumTotalRevenue(), 0.0));
        data.put("todayOrders", safeQuery(() -> statsMapper.countTodayOrders(), 0));
        data.put("todayRevenue", safeQuery(() -> statsMapper.sumTodayRevenue(), 0.0));
        data.put("totalPosts", safeQuery(() -> statsMapper.countTotalPosts(), 0));
        data.put("todayPosts", safeQuery(() -> statsMapper.countTodayPosts(), 0));
        data.put("collectTime", LocalDateTime.now().toString());
        return data;
    }

    private <T> T safeQuery(java.util.function.Supplier<T> supplier, T defaultValue) {
        try {
            T result = supplier.get();
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.warn("查询失败: {}", e.getMessage());
            return defaultValue;
        }
    }

    // ==================== Prompt 构建 ====================

    private String buildAuditSystemPrompt() {
        return """
            你是八方充电平台的智能审核助手。根据审核记录内容给出专业审核建议。

            输出格式要求（JSON + Markdown）：
            ```json
            {
                "suggestion": "APPROVE 或 REJECT 或 NEED_MORE_INFO",
                "confidence": 85,
                "priority": 1,
                "reason": "审核建议的原因说明",
                "actions": [
                    {
                        "actionType": "APPROVE_AUDIT",
                        "actionName": "通过审核",
                        "description": "动作描述",
                        "params": { "auditId": 123, "opinion": "审核意见" },
                        "autoExecutable": true
                    }
                ]
            }
            ```
            JSON后请用Markdown给出详细分析。
            """;
    }

    private String buildAuditUserPrompt(Map<String, Object> inputData) {
        return "# 审核记录信息\n```json\n" + gson.toJson(inputData) + "\n```\n请给出审核建议。";
    }

    private String buildAnomalySystemPrompt() {
        return """
            你是八方充电平台的智能监控系统，分析数据检测异常。

            输出格式：
            ```json
            {
                "hasAnomaly": true,
                "confidence": 85,
                "priority": 2,
                "anomalies": [{"type": "异常类型", "description": "描述", "severity": "HIGH"}],
                "actions": [{"actionType": "CREATE_ALERT", "actionName": "创建预警", "description": "", "params": {}, "autoExecutable": true}]
            }
            ```
            """;
    }

    private String buildAnomalyUserPrompt(Map<String, Object> inputData) {
        return "# 平台实时数据\n```json\n" + gson.toJson(inputData) + "\n```\n请检测异常。";
    }

    private String buildOptimizationSystemPrompt() {
        return """
            你是八方充电平台的运营优化顾问，提供具体可执行的优化建议。

            输出格式：
            ```json
            {
                "confidence": 80,
                "priority": 1,
                "suggestions": [{"category": "类别", "title": "标题", "description": "描述", "priority": 1}],
                "actions": [{"actionType": "SEND_NOTIFICATION", "actionName": "发送通知", "description": "", "params": {}, "autoExecutable": false}]
            }
            ```
            """;
    }

    private String buildOptimizationUserPrompt(Map<String, Object> inputData) {
        return "# 平台运营数据\n```json\n" + gson.toJson(inputData) + "\n```\n请提供优化建议。";
    }

    private String buildRiskSystemPrompt() {
        return """
            你是八方充电平台的风险控制专家，评估业务风险等级并给出控制建议。

            输出格式：
            ```json
            {
                "riskLevel": "HIGH/MEDIUM/LOW/NONE",
                "confidence": 85,
                "priority": 2,
                "riskFactors": [{"factor": "风险因素", "description": "描述"}],
                "actions": [{"actionType": "DISABLE_USER", "actionName": "禁用用户", "description": "", "params": {}, "autoExecutable": true}]
            }
            ```
            """;
    }

    private String buildRiskUserPrompt(Map<String, Object> inputData) {
        return "# 风险评估请求\n```json\n" + gson.toJson(inputData) + "\n```\n请评估风险。";
    }

    // ==================== 解析方法 ====================

    private List<AIDecisionDetailVO.SuggestedActionVO> parseActionsFromResponse(String aiResponse) {
        List<AIDecisionDetailVO.SuggestedActionVO> actionList = new ArrayList<>();
        try {
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                Map<String, Object> result = gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>(){}.getType());
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> actions = (List<Map<String, Object>>) result.get("actions");
                if (actions != null) {
                    for (Map<String, Object> action : actions) {
                        AIDecisionDetailVO.SuggestedActionVO vo = new AIDecisionDetailVO.SuggestedActionVO();
                        vo.setActionType((String) action.get("actionType"));
                        vo.setActionName((String) action.get("actionName"));
                        vo.setDescription((String) action.get("description"));
                        vo.setParams((Map<String, Object>) action.get("params"));
                        vo.setPriority(action.get("priority") != null ? ((Number) action.get("priority")).intValue() : 1);
                        vo.setAutoExecutable(Boolean.TRUE.equals(action.get("autoExecutable")));
                        actionList.add(vo);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析动作列表失败: {}", e.getMessage());
        }
        return actionList;
    }

    private double extractConfidence(String aiResponse) {
        try {
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                Map<String, Object> result = gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>(){}.getType());
                Object confidence = result.get("confidence");
                if (confidence instanceof Number) {
                    return ((Number) confidence).doubleValue();
                }
            }
        } catch (Exception e) {
            log.warn("提取置信度失败");
        }
        return 50.0;
    }

    private int extractPriority(String aiResponse) {
        try {
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                Map<String, Object> result = gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>(){}.getType());
                Object priority = result.get("priority");
                if (priority instanceof Number) {
                    return ((Number) priority).intValue();
                }
            }
        } catch (Exception e) {
            log.warn("提取优先级失败");
        }
        return 1;
    }

    private String extractJson(String text) {
        int start = text.indexOf("```json");
        if (start >= 0) {
            start = text.indexOf("\n", start) + 1;
            int end = text.indexOf("```", start);
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        int braceStart = text.indexOf("{");
        int braceEnd = text.lastIndexOf("}");
        if (braceStart >= 0 && braceEnd > braceStart) {
            return text.substring(braceStart, braceEnd + 1);
        }
        return null;
    }

    private String getAuditTypeName(Integer auditType) {
        if (auditType == null) return "未知";
        switch (auditType) {
            case 0: return "私桩认证";
            case 1: return "车主认证";
            case 2: return "充电桩投诉";
            case 3: return "充电桩故障";
            case 4: return "评论举报";
            case 5: return "帖子举报";
            case 6: return "交易申诉";
            default: return "未知";
        }
    }
}
