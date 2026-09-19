CREATE TABLE `system_notice` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` INT DEFAULT NULL COMMENT '接收用户ID，NULL表示全体用户',
  `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `type` TINYINT NOT NULL COMMENT '通知类型：0-系统公告，1-充电通知，2-预约通知，3-V2V通知，4-紧急呼叫通知，5-订单通知，6-审核通知',
  `related_id` INT DEFAULT NULL COMMENT '关联业务ID',
  `related_type` TINYINT DEFAULT NULL COMMENT '关联业务类型：0-订单，1-预约，2-V2V呼叫，3-紧急呼叫，4-帖子，5-评价',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';