package com.baffang.admin.service.impl;

import com.baffang.admin.dto.AIAnalysisRequest;
import com.baffang.admin.dto.AIAnalysisResponse;
import com.baffang.admin.mapper.StatsMapper;
import com.baffang.admin.service.AIAnalysisService;
import com.baffang.common.util.DeepSeekClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

/**
 * AI 数据分析服务实现
 */
@Slf4j
@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    @Autowired
    private StatsMapper statsMapper;

    @Autowired
    private DeepSeekClient deepSeekClient;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Override
    public AIAnalysisResponse analyzeAll() {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 收集所有统计数据
            log.info("开始收集统计数据...");
            Map<String, Object> allStats = collectAllStats();
            log.info("数据收集完成，耗时: {}ms", System.currentTimeMillis() - startTime);

            // 2. 构建提示词
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildFullAnalysisPrompt(allStats);

            // 3. 调用 AI 分析
            log.info("调用 DeepSeek 进行分析...");
            String analysis = deepSeekClient.chat(systemPrompt, userPrompt);
            log.info("AI 分析完成，总耗时: {}ms", System.currentTimeMillis() - startTime);

            return AIAnalysisResponse.builder()
                    .success(true)
                    .analysis(analysis)
                    .rawData(allStats)
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("AI 分析失败", e);
            return AIAnalysisResponse.builder()
                    .success(false)
                    .error("分析失败: " + e.getMessage())
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public AIAnalysisResponse analyzeModule(AIAnalysisRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> allStats = collectAllStats();
            Map<String, Object> moduleData = extractModuleData(request.getModule(), allStats);

            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildModuleAnalysisPrompt(request.getModule(), moduleData, request.getQuestion());

            String analysis = deepSeekClient.chat(systemPrompt, userPrompt);

            return AIAnalysisResponse.builder()
                    .success(true)
                    .analysis(analysis)
                    .module(request.getModule())
                    .question(request.getQuestion())
                    .rawData(Boolean.TRUE.equals(request.getIncludeRawData()) ? moduleData : null)
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("模块分析失败: module={}", request.getModule(), e);
            return AIAnalysisResponse.builder()
                    .success(false)
                    .error("分析失败: " + e.getMessage())
                    .module(request.getModule())
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public AIAnalysisResponse analyzeWithQuestion(String question) {
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> allStats = collectAllStats();

            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildQuestionPrompt(allStats, question);

            String analysis = deepSeekClient.chat(systemPrompt, userPrompt);

            return AIAnalysisResponse.builder()
                    .success(true)
                    .analysis(analysis)
                    .question(question)
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("问题分析失败: question={}", question, e);
            return AIAnalysisResponse.builder()
                    .success(false)
                    .error("分析失败: " + e.getMessage())
                    .question(question)
                    .analysisTime(LocalDateTime.now())
                    .costMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    // ==================== 安全查询工具方法 ====================

    /**
     * 安全收集数据，出错时返回错误信息而不是抛异常
     */
    private Map<String, Object> safeCollect(String module, Supplier<Map<String, Object>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.warn("收集 {} 数据失败: {}", module, e.getMessage());
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "数据收集失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 安全查询，出错时返回默认值
     */
    private <T> T safeQuery(Supplier<T> supplier, T defaultValue) {
        try {
            T result = supplier.get();
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.warn("查询失败: {}", e.getMessage());
            return defaultValue;
        }
    }

    // ==================== 数据收集 ====================

    /**
     * 收集所有统计数据
     */
    private Map<String, Object> collectAllStats() {
        Map<String, Object> allStats = new LinkedHashMap<>();

        allStats.put("platform", safeCollect("platform", this::collectPlatformStats));
        allStats.put("privateStation", safeCollect("privateStation", this::collectStationDistribution));
        allStats.put("vehicleAudit", safeCollect("vehicleAudit", this::collectVehicleAuditStats));
        allStats.put("auditAndComplaint", safeCollect("auditAndComplaint", this::collectComplaintStats));
        allStats.put("auditRecord", safeCollect("auditRecord", this::collectAuditRecordStats));
        allStats.put("forum", safeCollect("forum", this::collectForumStats));
        allStats.put("emergency", safeCollect("emergency", this::collectEmergencyStats));
        allStats.put("v2v", safeCollect("v2v", this::collectV2VStats));
        allStats.put("order", safeCollect("order", this::collectOrderStats));
        allStats.put("conversation", safeCollect("conversation", this::collectCustomerServiceStats));
        allStats.put("admin", safeCollect("admin", this::collectAdminStats));
        allStats.put("collectTime", LocalDateTime.now().toString());

        return allStats;
    }

    private Map<String, Object> collectPlatformStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", safeQuery(() -> statsMapper.countTotalUsers(), 0));
        stats.put("todayNewUsers", safeQuery(() -> statsMapper.countTodayNewUsers(), 0));
        stats.put("totalPrivateStations", safeQuery(() -> statsMapper.countTotalStations(), 0));
        stats.put("totalPublicStations", safeQuery(() -> statsMapper.countTotalPiles(), 0));
        stats.put("totalOrders", safeQuery(() -> statsMapper.countTotalOrders(), 0));
        stats.put("todayOrders", safeQuery(() -> statsMapper.countTodayOrders(), 0));
        stats.put("totalRevenue", safeQuery(() -> statsMapper.sumTotalRevenue(), 0.0));
        stats.put("todayRevenue", safeQuery(() -> statsMapper.sumTodayRevenue(), 0.0));
        stats.put("totalChargeAmount", safeQuery(() -> statsMapper.sumTotalChargeAmount(), 0.0));
        stats.put("todayChargeAmount", safeQuery(() -> statsMapper.sumTodayChargeAmount(), 0.0));
        return stats;
    }

    private Map<String, Object> collectStationDistribution() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byAuthStatus", safeQuery(() -> statsMapper.countStationsByRegion(), new ArrayList<>()));
        stats.put("byStatus", safeQuery(() -> statsMapper.countStationsByStatus(), new ArrayList<>()));
        stats.put("byType", safeQuery(() -> statsMapper.countStationsByOperator(), new ArrayList<>()));
        return stats;
    }

    private Map<String, Object> collectVehicleAuditStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byStatus", safeQuery(() -> statsMapper.countVehicleAuditByStatus(), new ArrayList<>()));
        stats.put("todayProcessed", safeQuery(() -> statsMapper.countTodayProcessedVehicleAudit(), 0));
        stats.put("avgProcessTimeHours", safeQuery(() -> statsMapper.avgVehicleAuditProcessTime(), null));
        return stats;
    }

    private Map<String, Object> collectComplaintStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byStatus", safeQuery(() -> statsMapper.countComplaintByStatus(), new ArrayList<>()));
        stats.put("byType", safeQuery(() -> statsMapper.countComplaintByType(), new ArrayList<>()));
        stats.put("todayNew", safeQuery(() -> statsMapper.countTodayNewComplaints(), 0));
        stats.put("avgProcessTimeHours", safeQuery(() -> statsMapper.avgComplaintProcessTime(), null));
        return stats;
    }

    private Map<String, Object> collectAuditRecordStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byTypeAndStatus", safeQuery(() -> statsMapper.countAuditRecordByTypeAndStatus(), new ArrayList<>()));
        stats.put("pendingTotal", safeQuery(() -> statsMapper.countPendingAuditRecords(), 0));
        return stats;
    }

    private Map<String, Object> collectForumStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPosts", safeQuery(() -> statsMapper.countTotalPosts(), 0));
        stats.put("todayPosts", safeQuery(() -> statsMapper.countTodayPosts(), 0));
        stats.put("totalComments", safeQuery(() -> statsMapper.countTotalComments(), 0));
        stats.put("todayComments", safeQuery(() -> statsMapper.countTodayComments(), 0));
        stats.put("byCategory", safeQuery(() -> statsMapper.countPostsByCategory(), new ArrayList<>()));
        return stats;
    }

    private Map<String, Object> collectEmergencyStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byStatus", safeQuery(() -> statsMapper.countEmergencyByStatus(), new ArrayList<>()));
        stats.put("todayCalls", safeQuery(() -> statsMapper.countTodayEmergencyCalls(), 0));
        stats.put("avgResponseTimeMinutes", safeQuery(() -> statsMapper.avgEmergencyResponseTime(), null));
        return stats;
    }

    private Map<String, Object> collectV2VStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byStatus", safeQuery(() -> statsMapper.countV2VByStatus(), new ArrayList<>()));
        stats.put("todayCalls", safeQuery(() -> statsMapper.countTodayV2VCalls(), 0));
        return stats;
    }

    private Map<String, Object> collectOrderStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byType", safeQuery(() -> statsMapper.countOrdersByType(), new ArrayList<>()));
        stats.put("byStatus", safeQuery(() -> statsMapper.countOrdersByStatus(), new ArrayList<>()));
        stats.put("reserveByStatus", safeQuery(() -> statsMapper.countReserveByStatus(), new ArrayList<>()));
        stats.put("chargeRateStats", safeQuery(() -> statsMapper.getChargeRateStats(), new ArrayList<>()));
        return stats;
    }

    private Map<String, Object> collectCustomerServiceStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("todaySessions", safeQuery(() -> statsMapper.countTodayCsSessions(), 0));
        stats.put("pendingSessions", safeQuery(() -> statsMapper.countPendingCsSessions(), 0));
        stats.put("todayMessages", safeQuery(() -> statsMapper.countTodayCsMessages(), 0));
        stats.put("avgResponseTimeSeconds", safeQuery(() -> statsMapper.avgCsResponseTime(), null));
        stats.put("adminWorkload", safeQuery(() -> statsMapper.countCsWorkloadByAdmin(), new ArrayList<>()));
        return stats;
    }

    private Map<String, Object> collectAdminStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("byType", safeQuery(() -> statsMapper.countAdminsByType(), new ArrayList<>()));
        return stats;
    }

    // ==================== 提示词构建 ====================

    private String buildSystemPrompt() {
        return """
            你是八方充电平台的资深数据分析专家。你的职责是分析平台运营数据，提供专业、深入、可操作的洞察和建议。

            ## 分析原则：
            1. **数据驱动**：所有结论必须有数据支撑，引用具体数值
            2. **结构清晰**：使用标题、列表、表格等格式化输出
            3. **重点突出**：优先关注异常数据和关键指标
            4. **可操作性**：给出具体、可执行的改进建议
            5. **专业表达**：使用运营和数据分析领域的专业术语

            ## 输出格式要求（Markdown）：
            - 使用 emoji 增强可读性
            - 关键数字用 **粗体** 标注
            - 同比/环比变化用 ↑↓ 箭头标识
            - 问题用 ⚠️ 标注
            - 建议用 💡 标注
            - 重点关注用 🎯 标注

            ## 分析维度参考：
            - 用户增长与留存
            - 业务量与收入趋势
            - 运营效率（审核、客服、投诉处理）
            - 设备利用率
            - 服务质量与用户体验
            - 风险预警与异常识别
            """;
    }

    private String buildFullAnalysisPrompt(Map<String, Object> allStats) {
        StringBuilder sb = new StringBuilder();

        sb.append("# 八方充电平台运营数据 - 请进行全面分析\n\n");
        sb.append("## 📊 原始数据\n\n");
        sb.append("```json\n").append(gson.toJson(allStats)).append("\n```\n\n");

        sb.append("## 📝 分析要求\n\n");
        sb.append("请从以下维度进行全面分析，生成一份运营分析报告：\n\n");
        sb.append("### 1. 平台整体概况\n");
        sb.append("- 核心指标汇总（用户、订单、收入、充电量）\n");
        sb.append("- 今日数据与历史数据对比\n\n");

        sb.append("### 2. 充电站运营分析\n");
        sb.append("- 私桩认证状态分布\n");
        sb.append("- 各状态充电站占比\n");
        sb.append("- 私桩类型分布\n\n");

        sb.append("### 3. 审核与风控\n");
        sb.append("- 车辆认证审核效率\n");
        sb.append("- 各类审核积压情况\n");
        sb.append("- 投诉/举报处理时效\n\n");

        sb.append("### 4. 用户服务质量\n");
        sb.append("- V2V互助充电情况\n");
        sb.append("- 紧急呼叫处理情况\n");
        sb.append("- 对话/客服响应\n\n");

        sb.append("### 5. 社区运营\n");
        sb.append("- 论坛活跃度\n");
        sb.append("- 内容分布\n\n");

        sb.append("### 6. 订单与交易\n");
        sb.append("- 订单类型分布\n");
        sb.append("- 订单状态分析\n");
        sb.append("- 充电桩评分情况\n\n");

        sb.append("### 7. 问题预警\n");
        sb.append("- 识别数据中的异常\n");
        sb.append("- 潜在风险点\n\n");

        sb.append("### 8. 优化建议\n");
        sb.append("- 短期改进措施（1周内可执行）\n");
        sb.append("- 中期优化方向（1-3个月）\n");

        return sb.toString();
    }

    private String buildModuleAnalysisPrompt(String module, Map<String, Object> moduleData, String question) {
        StringBuilder sb = new StringBuilder();

        String moduleName = getModuleName(module);
        sb.append("# ").append(moduleName).append(" - 深度分析\n\n");

        sb.append("## 📊 数据\n\n");
        sb.append("```json\n").append(gson.toJson(moduleData)).append("\n```\n\n");

        if (question != null && !question.trim().isEmpty()) {
            sb.append("## ❓ 重点问题\n\n");
            sb.append(question).append("\n\n");
        }

        sb.append("## 📝 分析要求\n\n");
        sb.append("请针对【").append(moduleName).append("】进行深入分析：\n\n");
        sb.append("1. 当前状态评估\n");
        sb.append("2. 关键指标解读\n");
        sb.append("3. 存在的问题\n");
        sb.append("4. 具体改进建议\n");

        return sb.toString();
    }

    private String buildQuestionPrompt(Map<String, Object> allStats, String question) {
        StringBuilder sb = new StringBuilder();

        sb.append("# 数据分析问答\n\n");
        sb.append("## 📊 平台数据\n\n");
        sb.append("```json\n").append(gson.toJson(allStats)).append("\n```\n\n");
        sb.append("## ❓ 问题\n\n");
        sb.append(question).append("\n\n");
        sb.append("## 📝 要求\n\n");
        sb.append("请基于以上数据，专业、详细地回答问题。如果数据不足以回答，请说明还需要哪些数据。\n");

        return sb.toString();
    }

    // ==================== 工具方法 ====================

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractModuleData(String module, Map<String, Object> allStats) {
        if (module == null || "all".equals(module)) {
            return allStats;
        }

        Object data = allStats.get(module);
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }

        Map<String, Object> result = new HashMap<>();
        result.put(module, data);
        return result;
    }

    private String getModuleName(String module) {
        if (module == null) return "全部";
        switch (module) {
            case "platform": return "平台总览";
            case "privateStation": return "私桩管理";
            case "vehicleAudit": return "车辆认证审核";
            case "auditAndComplaint": return "投诉举报管理";
            case "auditRecord": return "审核记录";
            case "forum": return "论坛社区";
            case "emergency": return "紧急呼叫";
            case "v2v": return "V2V互助充电";
            case "order": return "订单交易";
            case "conversation": return "对话客服";
            case "admin": return "管理员";
            default: return module;
        }
    }
}
