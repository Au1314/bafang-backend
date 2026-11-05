-- 紧急呼叫处理日志表（2026-11 安全与功能整改新增）
-- 用途：响应员处理紧急呼叫过程中添加的日志，持久化到数据库（此前前端仅存本地数组，刷新即失）
CREATE TABLE IF NOT EXISTS `emergency_handle_log` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `call_id` INT NOT NULL COMMENT '紧急呼叫ID（关联 emergency_call.id）',
  `admin_id` INT DEFAULT NULL COMMENT '操作管理员ID（关联 admin_info.id）',
  `content` VARCHAR(500) NOT NULL COMMENT '日志内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_call_id` (`call_id`),
  KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急呼叫处理日志表';
