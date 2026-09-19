package com.baffang.config;

import com.baffang.common.constant.AIDecisionTriggerType;
import com.baffang.admin.mapper.AIDecisionRecordMapper;
import com.baffang.admin.service.AIDecisionService;
import com.baffang.admin.vo.AIDecisionResponseVO;
import com.baffang.common.entity.AIDecisionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AIDecisionScheduler {

    private final AIDecisionService aiDecisionService;
    private final AIDecisionRecordMapper decisionRecordMapper;

    /**
     * 每小时执行异常检测
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledAnomalyDetection() {
        log.info("【定时任务】开始执行异常检测...");
        try {
            // 使用定时任务触发类型
            AIDecisionResponseVO response = aiDecisionService.detectAnomalies(AIDecisionTriggerType.SCHEDULED);
            if (response.getSuccess()) {
                log.info("【定时任务】异常检测完成，决策编号: {}", response.getDecisionNo());
            } else {
                log.warn("【定时任务】异常检测失败: {}", response.getError());
            }
        } catch (Exception e) {
            log.error("【定时任务】异常检测执行异常", e);
        }
    }

    /**
     * 每天早上9点生成运营优化建议
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduledOptimizationSuggestion() {
        log.info("【定时任务】开始生成运营优化建议...");
        try {
            // 使用定时任务触发类型
            AIDecisionResponseVO response = aiDecisionService.suggestOptimization(AIDecisionTriggerType.SCHEDULED);
            if (response.getSuccess()) {
                log.info("【定时任务】运营优化建议生成完成，决策编号: {}", response.getDecisionNo());
            }
        } catch (Exception e) {
            log.error("【定时任务】运营优化建议生成异常", e);
        }
    }

    /**
     * 每5分钟检查并执行待自动执行的决策
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void scheduledAutoExecuteDecisions() {
        try {
            List<AIDecisionRecord> pendingRecords = decisionRecordMapper
                    .selectPendingAutoExecute(BigDecimal.valueOf(80.0));

            for (AIDecisionRecord record : pendingRecords) {
                log.info("【定时任务】自动执行决策: {}", record.getDecisionNo());
                aiDecisionService.executeActions(record.getId());
            }

            if (!pendingRecords.isEmpty()) {
                log.info("【定时任务】自动执行了 {} 个决策", pendingRecords.size());
            }
        } catch (Exception e) {
            log.error("【定时任务】自动执行决策异常", e);
        }
    }
}
