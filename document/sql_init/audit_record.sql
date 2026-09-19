-- 审核记录主表（audit_record）
CREATE TABLE IF NOT EXISTS `audit_record` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
    `audit_no` VARCHAR(32) NULL DEFAULT NULL COMMENT '审核单号',
    `audit_type` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '审核类型：0 - 私桩认证，1 - 车主认证，2 - 充电桩投诉，3 - 充电桩故障，4 - 评论举报，5 - 帖子举报，6 - 交易申诉',
    `business_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '业务 ID（关联对应业务表的主键）',
    `applicant_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '申请人 / 提交人 ID（关联 user_info 表 id）',
    `title` VARCHAR(100) NULL DEFAULT NULL COMMENT '审核标题 / 摘要',
    `content` TEXT NULL DEFAULT NULL COMMENT '审核内容 / 详情描述',
    `attachments` VARCHAR(1000) NULL DEFAULT NULL COMMENT '附件 URL，多个用逗号分隔',
    `current_status` TINYINT UNSIGNED NULL DEFAULT 0 COMMENT '当前状态：0 - 待审核，1 - 审核中，2 - 已通过，3 - 已拒绝，4 - 需补充材料，5 - 已撤回（默认 0）',
    `current_admin_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '当前处理人 ID（关联 admin_info 表 id）',
    `priority` TINYINT UNSIGNED NULL DEFAULT 1 COMMENT '优先级：0 - 低，1 - 普通，2 - 高，3 - 紧急（默认 1）',
    `submit_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间（默认当前时间）',
    `finish_time` DATETIME NULL DEFAULT NULL COMMENT '完成时间',
    `final_opinion` VARCHAR(500) NULL DEFAULT NULL COMMENT '最终审核意见',
    `reject_reason` VARCHAR(255) NULL DEFAULT NULL COMMENT '拒绝原因',
    `is_deleted` TINYINT UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除标识：0 - 正常，1 - 已删除（默认 0）',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（默认当前时间，更新时自动刷新）',
    -- 主键索引
    PRIMARY KEY (`id`),
    -- 常用查询索引
    INDEX idx_audit_no (`audit_no`),
    INDEX idx_audit_type (`audit_type`),
    INDEX idx_business_id (`business_id`),
    INDEX idx_applicant_id (`applicant_id`),
    INDEX idx_current_status (`current_status`),
    INDEX idx_current_admin_id (`current_admin_id`),
    INDEX idx_priority (`priority`),
    INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核记录主表';