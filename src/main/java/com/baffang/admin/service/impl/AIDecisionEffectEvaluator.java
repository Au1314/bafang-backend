package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.AIDecisionRecordMapper;
import com.baffang.admin.mapper.StatsMapper;
import com.baffang.common.entity.AIDecisionRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 决策效果评估器（面试叙事整改：补齐"生成→审核→执行→评估"闭环的最后一环）
 *
 * 评估思路（诚实版）：
 * 决策执行对平台指标的影响需要"执行前指标快照"才能精确归因，而当前系统未做指标快照；
 * 因此本评估器采用**启发式平台健康度评分**——评估时点抓取三项核心运营指标
 * （待审核数 / 待处理投诉 / 今日紧急呼叫），按决策业务类型加权折算 0-100 健康分，
 * 连同指标快照写入决策记录（effect_evaluated/effect_score/effect_detail）。
 * 演进项：引入 platform_metric_snapshot 指标快照表后，可升级为执行前后对比的精确归因。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIDecisionEffectEvaluator {

    /** 执行完成满 3 天才评估（给决策生效留时间窗） */
    private static final int EVALUATE_AFTER_DAYS = 3;

    private final AIDecisionRecordMapper decisionRecordMapper;
    private final StatsMapper statsMapper;

    /**
     * 扫描并评估已执行完成的决策（由调度器每日调用）
     *
     * @return 本次评估的决策数量
     */
    public int evaluatePendingDecisions() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(EVALUATE_AFTER_DAYS);
        List<AIDecisionRecord> records = decisionRecordMapper.selectExecutedForEvaluation(cutoff);

        for (AIDecisionRecord record : records) {
            try {
                evaluate(record);
            } catch (Exception e) {
                log.error("决策[{}]效果评估失败", record.getDecisionNo(), e);
            }
        }
        return records.size();
    }

    private void evaluate(AIDecisionRecord record) {
        // 1. 抓取评估时点的平台健康指标（StatsMapper 统计 SQL 均为已实现的真实查询）
        Integer pendingAudit = nz(statsMapper.countPendingAuditRecords());
        Integer todayComplaints = nz(statsMapper.countTodayNewComplaints());
        Integer todayEmergency = nz(statsMapper.countTodayEmergencyCalls());

        // 2. 启发式健康评分：三项指标按权重扣分（待审核×3、投诉×4、紧急×5），下限 0
        int score = Math.max(0, Math.min(100,
                100 - pendingAudit * 3 - todayComplaints * 4 - todayEmergency * 5));

        // 3. 按决策业务类型补充关联性说明
        String domain = switch (nz(record.getDecisionType())) {
            case 0 -> "审核建议：请关注待审核数是否较决策前下降";
            case 1 -> "异常预警：请关注异常数据是否复发";
            case 2 -> "资源调度：请关注高峰时段桩位利用率";
            case 3 -> "风险控制：请关注风险事件是否复发";
            case 4 -> "运营优化：请关注今日活跃与新增用户";
            default -> "通用";
        };

        String detail = String.format(
                "评估时点=%s | 待审核=%d, 今日新投诉=%d, 今日紧急呼叫=%d | 启发式健康分=%d | 业务关联：%s | "
                        + "注：精确归因需指标快照表（演进项）",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                pendingAudit, todayComplaints, todayEmergency, score, domain);

        decisionRecordMapper.updateEffect(record.getId(), java.math.BigDecimal.valueOf(score), detail);
        log.info("决策[{}]效果评估完成：score={}, pendingAudit={}, complaints={}",
                record.getDecisionNo(), score, pendingAudit, todayComplaints);
    }

    private int nz(Integer v) {
        return v == null ? 0 : v;
    }
}
