-- 平台参数配置表（2026-11 契约补齐新增）
-- 用途：平台管理页"参数配置"的持久化（此前前端仅存 localStorage）
-- 设计：单行配置（id=1），config_json 存 JSON，扩展无需改表
CREATE TABLE IF NOT EXISTS `platform_config` (
  `id` INT NOT NULL DEFAULT 1 COMMENT '主键（固定为1，单行配置）',
  `config_json` TEXT NOT NULL COMMENT '配置内容（JSON 对象）',
  `update_admin_id` INT DEFAULT NULL COMMENT '最后修改管理员ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台参数配置表';

-- 初始化默认配置（前端 configForm 的初始值）
INSERT INTO `platform_config` (`id`, `config_json`)
VALUES (1, '{"defaultCallRange":"","creditReward":0,"logFields":["操作时间","操作人","操作类型"]}')
ON DUPLICATE KEY UPDATE `id` = `id`;
