CREATE TABLE `charge_rate` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int DEFAULT NULL COMMENT '评价用户id(关联用户表id)',
  `type` tinyint DEFAULT NULL COMMENT '0：公桩评价，1：私桩评价',
  `charging_station_id` int DEFAULT NULL COMMENT '评价充电桩id',
  `anonymous` tinyint DEFAULT NULL COMMENT '是否匿名 0：否，1：是',
  `score` tinyint DEFAULT NULL COMMENT '评价分数，1-5分',
  `comment` text COMMENT '评价内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT NULL COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电桩评价表';