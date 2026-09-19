-- AI 决策规则配置表
CREATE TABLE `ai_decision_rule` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `decision_type` TINYINT NOT NULL COMMENT '决策类型',
    `trigger_condition` JSON COMMENT '触发条件',
    `auto_execute` TINYINT DEFAULT 0 COMMENT '是否自动执行：0-否，1-是',
    `execute_threshold` DECIMAL(5,2) DEFAULT 80.00 COMMENT '自动执行阈值（置信度>=此值才自动执行）',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '规则描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI决策规则配置表';


-- 插入默认规则
INSERT INTO `ai_decision_rule` (`rule_code`, `rule_name`, `decision_type`, `auto_execute`, `execute_threshold`, `description`) VALUES
('AUDIT_SUGGESTION', '审核建议', 0, 0, 90.00, '对待审核事项提供AI建议'),
('ANOMALY_WARNING', '异常预警', 1, 1, 85.00, '检测到异常数据自动预警'),
('RESOURCE_SCHEDULE', '资源调度', 2, 0, 80.00, '高峰期资源调度建议'),
('RISK_CONTROL', '风险控制', 3, 1, 90.00, '风险事件自动处置'),
('OPERATION_OPTIMIZE', '运营优化', 4, 0, 75.00, '运营优化建议');