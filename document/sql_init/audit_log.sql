-- 审核操作日志表（audit_log）
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
    `audit_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '审核记录 ID（关联 audit_record 表 id）',
    `admin_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '操作人 ID（关联 admin_info 表 id）',
    `action` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '操作类型：0 - 提交审核，1 - 审核通过，2 - 审核拒绝，3 - 要求补充材料，4 - 转交他人，5 - 撤回，6 - 重新提交',
    `from_status` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '操作前状态',
    `to_status` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '操作后状态',
    `opinion` VARCHAR(500) NULL DEFAULT NULL COMMENT '审核意见 / 操作备注',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间（默认当前时间）',
    -- 主键索引
    PRIMARY KEY (`id`),
    -- 常用查询索引
    INDEX idx_audit_id (`audit_id`),
    INDEX idx_admin_id (`admin_id`),
    INDEX idx_action (`action`),
    INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核操作日志表';