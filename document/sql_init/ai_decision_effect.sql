-- AI 决策效果评估字段（2026-11 面试叙事整改：补齐"生成→执行→评估"闭环）
ALTER TABLE `ai_decision_record`
    ADD COLUMN `effect_evaluated` TINYINT NOT NULL DEFAULT 0 COMMENT '效果是否已评估：0-未评估，1-已评估',
    ADD COLUMN `effect_score` DECIMAL(5,2) DEFAULT NULL COMMENT '效果评分（0-100，启发式平台健康度评分）',
    ADD COLUMN `effect_detail` VARCHAR(1000) DEFAULT NULL COMMENT '效果评估详情（评估时各项平台指标快照）';
