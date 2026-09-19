-- 邀请码表（admin_invite_code）
CREATE TABLE IF NOT EXISTS `admin_invite_code` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
    `code` VARCHAR(32) NULL DEFAULT NULL COMMENT '邀请码',
    `created_by` INT UNSIGNED NULL DEFAULT NULL COMMENT '创建人 ID（超级管理员）',
    `target_type` TINYINT UNSIGNED NULL DEFAULT 10 COMMENT '注册后的管理员类型：0:超级管理员，1：普通管理员，2：审核员，3：客服，4：紧急响应人员，5：论坛管理员，10：未分配身份（默认10）',
    `max_uses` INT UNSIGNED NULL DEFAULT 1 COMMENT '最大使用次数（默认 1）',
    `used_count` INT UNSIGNED NULL DEFAULT 0 COMMENT '已使用次数（默认 0）',
    `expire_time` DATETIME NULL DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT UNSIGNED NULL DEFAULT 0 COMMENT '状态：0 - 有效，1 - 已禁用（默认 0）',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（默认当前时间，更新时自动刷新）',
    -- 主键索引
    PRIMARY KEY (`id`),
    -- 常用查询索引
    INDEX idx_code (`code`),
    INDEX idx_created_by (`created_by`),
    INDEX idx_target_type (`target_type`),
    INDEX idx_status (`status`),
    INDEX idx_expire_time (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邀请码表';