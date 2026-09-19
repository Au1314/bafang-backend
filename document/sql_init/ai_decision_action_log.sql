-- AI 决策动作执行日志
CREATE TABLE `ai_decision_action_log` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `decision_id` INT NOT NULL COMMENT '决策记录ID',
    `action_type` VARCHAR(50) NOT NULL COMMENT '动作类型',
    `action_name` VARCHAR(100) NOT NULL COMMENT '动作名称',
    `action_params` JSON COMMENT '动作参数',
    `execute_status` TINYINT DEFAULT 0 COMMENT '执行状态：0-待执行，1-执行中，2-成功，3-失败，4-跳过',
    `execute_result` TEXT COMMENT '执行结果',
    `error_msg` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `execute_time` DATETIME DEFAULT NULL COMMENT '执行时间',
    `cost_ms` INT DEFAULT NULL COMMENT '执行耗时（毫秒）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_decision_id` (`decision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI决策动作执行日志';