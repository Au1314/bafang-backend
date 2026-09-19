-- 系统日志表（system_log）
CREATE TABLE IF NOT EXISTS system_log (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
    `user_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '操作用户 ID（关联用户表 id）',
    `admin_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '操作管理员 ID（关联管理员表 id）',
    `module` VARCHAR(50) NULL DEFAULT NULL COMMENT '操作模块',
    `action` VARCHAR(100) NULL DEFAULT NULL COMMENT '操作行为',
    `method` VARCHAR(10) NULL DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `url` VARCHAR(255) NULL DEFAULT NULL COMMENT '请求 URL',
    `params` TEXT NULL DEFAULT NULL COMMENT '请求参数',
    `result` TEXT NULL DEFAULT NULL COMMENT '返回结果',
    `ip` VARCHAR(50) NULL DEFAULT NULL COMMENT '操作 IP',
    `user_agent` VARCHAR(500) NULL DEFAULT NULL COMMENT '用户代理',
    `status` TINYINT UNSIGNED NULL DEFAULT 1 COMMENT '操作状态：0 - 失败，1 - 成功（默认 1）',
    `error_msg` TEXT NULL DEFAULT NULL COMMENT '错误信息',
    `execution_time` INT UNSIGNED NULL DEFAULT NULL COMMENT '执行时长（毫秒）',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
    -- 主键索引
    PRIMARY KEY (`id`),
    -- 常用查询索引
    INDEX idx_module (`module`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_admin_id (`admin_id`),
    INDEX idx_create_time (`create_time`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';