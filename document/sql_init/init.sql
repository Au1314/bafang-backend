-- 用户表
CREATE TABLE `user_info` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `name` VARCHAR(50) DEFAULT '' COMMENT '昵称',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `type` TINYINT DEFAULT 0 COMMENT '用户类型,0：普通用户，1：vip 用户（暂不实装 vip）',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT '' COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像',
  `signature` VARCHAR(255) DEFAULT '' COMMENT '用户签名',
  `auth_charging_station` TINYINT DEFAULT 0 COMMENT '认证充电桩，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败',
  `auth_vehicle` TINYINT DEFAULT 0 COMMENT '认证车辆，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败',
  `privacy_vehicle` TINYINT DEFAULT 0 COMMENT '是否隐藏自己的车辆信息，0：公开，1：隐藏',
  `privacy_station` TINYINT DEFAULT 0 COMMENT '是否隐藏自己的充电桩信息，0：公开，1：隐藏',
  `points` INT DEFAULT 0 COMMENT '用户积分总数',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 管理员表
CREATE TABLE `admin_info` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `name` VARCHAR(50) DEFAULT '' COMMENT '昵称',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `type` TINYINT DEFAULT 10 COMMENT '管理员类型 0:超级管理员，1：普通管理员，2：审核员，3：客服，4：紧急响应人员，5：论坛管理员，10：未分配身份',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 私桩表
CREATE TABLE `private_charge_station` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) DEFAULT '' COMMENT '私桩名称',
  `type` TINYINT DEFAULT 0 COMMENT '0：普通私桩，1：专业私桩',
  `status` TINYINT DEFAULT 0 COMMENT '0：空闲，1：已预约，2：已充电，3：异常',
  `current_type` TINYINT DEFAULT 0 COMMENT '0：直流电，1：交流电',
  `price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '充电价格，单位：元/度',
  `price_type` TINYINT DEFAULT 0 COMMENT '定价类型：0：固定价格，1：动态价格',
  `location_name` VARCHAR(255) DEFAULT '' COMMENT '地点名称',
  `longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '经度（-180~180）',
  `latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度(-90~90）',
  `max_charge_voltage` INT DEFAULT NULL COMMENT '最大充电电压，单位：伏特',
  `max_charge_current` INT DEFAULT NULL COMMENT '最大充电电流，单位：安培',
  `max_charge_power` INT DEFAULT NULL COMMENT '最大充电功率，单位：瓦',
  `max_vehicles` INT DEFAULT 1 COMMENT '最大车辆数',
  `current_vehicles` INT DEFAULT 0 COMMENT '当前车辆数',
  `open_status` TINYINT DEFAULT 0 COMMENT '0：关闭，1：开启',
  `open_time` DATETIME DEFAULT NULL COMMENT '开启时间 只有当状态为开启时，才会有开启时间',
  `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `owner_id` INT DEFAULT NULL COMMENT '私桩所有者 id(关联用户表 id)',
  `auth_status` TINYINT DEFAULT 0 COMMENT '认证状态 0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败',
  `image` VARCHAR(255) DEFAULT '' COMMENT '私桩演示图片',
  `description` VARCHAR(500) DEFAULT '' COMMENT '私桩描述信息',
  `rating` DECIMAL(3, 2) DEFAULT 0.00 COMMENT '评分',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私桩表';

-- 公桩表
CREATE TABLE `public_charge_station` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) DEFAULT '' COMMENT '公桩名称',
  `location_name` VARCHAR(255) DEFAULT '' COMMENT '地点名称',
  `longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '经度（-180~180）',
  `latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度(-90~90）',
  `current_type` TINYINT DEFAULT 0 COMMENT '0：直流电，1：交流电',
  `price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '充电价格，单位：元/度',
  `max_charge_voltage` INT DEFAULT NULL COMMENT '最大充电电压，单位：伏特',
  `max_charge_current` INT DEFAULT NULL COMMENT '最大充电电流，单位：安培',
  `max_charge_power` INT DEFAULT NULL COMMENT '最大充电功率，单位：瓦',
  `max_vehicles` INT DEFAULT 1 COMMENT '最大车辆数',
  `current_vehicles` INT DEFAULT 0 COMMENT '当前车辆数',
  `open_status` TINYINT DEFAULT 0 COMMENT '0：关闭，1：开启',
  `open_time` DATETIME DEFAULT NULL COMMENT '开启时间',
  `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `provider` VARCHAR(100) DEFAULT '' COMMENT '公桩建设者',
  `image` VARCHAR(255) DEFAULT '' COMMENT '演示图片',
  `description` VARCHAR(500) DEFAULT '' COMMENT '描述信息',
  `rating` DECIMAL(3, 2) DEFAULT 0.00 COMMENT '评分',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公桩表';

-- 私桩预约表
CREATE TABLE `private_station_reserve` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `user_id` INT DEFAULT NULL COMMENT '预约用户 id(关联用户表 id)',
  `charging_station_id` INT DEFAULT NULL COMMENT '预约私桩 id(关联私桩表 id)',
  `station_owner_id` INT DEFAULT NULL COMMENT '私桩所有者 id(关联用户表 id)',
  `reserve_time` DATETIME DEFAULT NULL COMMENT '预约时间',
  `status` TINYINT DEFAULT 0 COMMENT '0：待确认，1：已确认，2：已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_charging_station_id` (`charging_station_id`),
  KEY `idx_station_owner_id` (`station_owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私桩预约表';

-- 车辆认证表
CREATE TABLE `car_auth` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `owner_id` INT DEFAULT NULL COMMENT '认证用户 id(关联用户表 id)',
  `car_name` VARCHAR(100) DEFAULT '' COMMENT '车名',
  `car_brand` VARCHAR(50) DEFAULT '' COMMENT '品牌',
  `car_type` VARCHAR(100) DEFAULT '' COMMENT '型号',
  `vehicle_type` VARCHAR(50) DEFAULT '' COMMENT '车辆类型 ',
  `vehicle_number` VARCHAR(20) DEFAULT '' COMMENT '车辆号码',
  `current_type` TINYINT DEFAULT 0 COMMENT '0：直流电，1：交流电',
  `max_charge_voltage` INT DEFAULT NULL COMMENT '最大充电电压，单位：伏特',
  `max_charge_current` INT DEFAULT NULL COMMENT '最大充电电流，单位：安培',
  `max_charge_power` INT DEFAULT NULL COMMENT '最大充电功率，单位：瓦',
  `battery_capacity` DECIMAL(10, 2) DEFAULT NULL COMMENT '电池容量，单位：千瓦时',
  `discharge_power` INT DEFAULT NULL COMMENT '放电功率，单位：瓦',
  `range` INT DEFAULT NULL COMMENT '续航，单位：公里',
  `auth_status` TINYINT DEFAULT 0 COMMENT '认证状态 0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆认证表';

-- V2V呼叫表
CREATE TABLE `v_to_v` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `request_user_id` INT DEFAULT NULL COMMENT '呼叫用户 id(关联用户表 id)',
  `response_user_id` INT DEFAULT NULL COMMENT '响应用户 id(关联用户表 id)',
  `call_range` DECIMAL(10, 2) DEFAULT NULL COMMENT '呼叫范围，单位：公里',
  `call_time` DATETIME DEFAULT NULL COMMENT '呼叫时间',
  `call_position` VARCHAR(255) DEFAULT '' COMMENT '呼叫位置',
  `call_longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '呼叫位置经度（-180~180）',
  `call_latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '呼叫位置纬度(-90~90）',
  `target_position` VARCHAR(255) DEFAULT '' COMMENT '目标位置(两位用户协商后确定的位置)',
  `target_longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '目标位置经度（-180~180）',
  `target_latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '目标位置纬度(-90~90）',
  `status` TINYINT DEFAULT 0 COMMENT '0：待响应，1：已响应，2：已完成，3：已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_request_user_id` (`request_user_id`),
  KEY `idx_response_user_id` (`response_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='V2V呼叫表';

-- 紧急呼叫表
CREATE TABLE `emergency_call` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `request_user_id` INT DEFAULT NULL COMMENT '呼叫用户 id(关联用户表 id)',
  `type` TINYINT DEFAULT 0 COMMENT '0：呼叫拖车，1：呼叫充电桩，2：其他呼叫',
  `response_admin_id` INT DEFAULT NULL COMMENT '响应管理员 id(关联管理员表 id)',
  `call_range` DECIMAL(10, 2) DEFAULT NULL COMMENT '呼叫范围，单位：公里',
  `call_time` DATETIME DEFAULT NULL COMMENT '呼叫时间',
  `call_position` VARCHAR(255) DEFAULT '' COMMENT '呼叫位置',
  `call_longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '呼叫位置经度（-180~180）',
  `call_latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '呼叫位置纬度(-90~90）',
  `target_position` VARCHAR(255) DEFAULT '' COMMENT '目标位置(两位用户协商后确定的位置)',
  `target_longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '目标位置经度（-180~180）',
  `target_latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '目标位置纬度(-90~90）',
  `status` TINYINT DEFAULT 0 COMMENT '0：待响应，1：已响应，2：已完成，3：已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_request_user_id` (`request_user_id`),
  KEY `idx_response_admin_id` (`response_admin_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急呼叫表';

-- 对话表
CREATE TABLE `conversation` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `type` TINYINT DEFAULT 0 COMMENT '对话类型：0-客服对话，1-预约对话，2-紧急呼叫对话，3-V2V 对话，4-普通用户聊天',
  `status` TINYINT DEFAULT 1 COMMENT '对话状态：1-进行中，2-已完成，3-已关闭',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `remark` VARCHAR(255) DEFAULT '' COMMENT '备注信息',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话表';

-- 对话参与者表
CREATE TABLE `conversation_participant` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `conversation_id` INT DEFAULT NULL COMMENT '对话 ID',
  `participant_id` INT DEFAULT NULL COMMENT '参与者 ID（用户 ID 或管理员 ID）',
  `participant_type` TINYINT DEFAULT 0 COMMENT '参与者类型：0-普通用户，1-管理员，2-系统',
  `role` TINYINT DEFAULT 0 COMMENT '参与者角色：0-发起者，1-响应者，2-观察者',
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `last_read_time` DATETIME DEFAULT NULL COMMENT '最后阅读时间',
  `unread_count` INT DEFAULT 0 COMMENT '未读消息数',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_participant_id` (`participant_id`),
  KEY `idx_participant_type` (`participant_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话参与者表';

-- 对话消息表
CREATE TABLE `conversation_message` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `conversation_id` INT DEFAULT NULL COMMENT '对话 ID',
  `sender_id` INT DEFAULT NULL COMMENT '发送者 ID',
  `sender_type` TINYINT DEFAULT 0 COMMENT '发送者类型：0-普通用户，1-管理员，2-系统',
  `message_type` TINYINT DEFAULT 0 COMMENT '消息类型：0-文本，1-图片，2-语音，3-系统通知',
  `content` TEXT DEFAULT NULL COMMENT '消息内容',
  `extra_info` JSON DEFAULT NULL COMMENT '扩展信息（如图片 URL、语音时长等）',
  `send_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_read` TINYINT DEFAULT 0 COMMENT ' 对方是否已读：0-未读，1-已读',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_sender_type` (`sender_type`),
  KEY `idx_message_type` (`message_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息表';



-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（非空）',
  `user_id` INT NOT NULL COMMENT '用户ID（关联用户表id，非空）',
  `order_type` TINYINT NOT NULL COMMENT '订单类型：0-公桩充电，1-私桩充电，2-V2V充电',
  `charging_station_id` INT NULL COMMENT '充电桩ID（可为空）',
  `charging_station_type` TINYINT NULL COMMENT '充电桩类型：0-公桩，1-私桩',
  `v2v_call_id` INT NULL COMMENT 'V2V呼叫ID（关联V2V呼叫表id）',
  `start_time` DATETIME NULL COMMENT '充电开始时间',
  `end_time` DATETIME NULL COMMENT '充电结束时间',
  `duration` INT NULL COMMENT '充电时长（分钟）',
  `electricity` DECIMAL(10,2) NULL COMMENT '充电电量（度）',
  `unit_price` DECIMAL(10,2) NULL COMMENT '单价（元/度）',
  `electricity_fee` DECIMAL(10,2) NULL COMMENT '电费（元）',
  `service_fee` DECIMAL(10,2) NULL COMMENT '服务费（元）',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额（元，默认0）',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额（元，非空）',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际支付金额（元，非空）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-充电中，2-充电完成待支付，3-已完成，4-已取消，5-已退款',
  `pay_time` DATETIME NULL COMMENT '支付时间',
  `pay_method` TINYINT NULL COMMENT '支付方式：0-微信，1-支付宝，2-余额，3-积分抵扣',
  `remark` VARCHAR(255) NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（更新时自动刷新）',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-正常，1-已删除',
  -- 主键索引
  PRIMARY KEY (`id`),
  -- 唯一索引：订单编号不重复
  UNIQUE KEY `uk_order_no` (`order_no`),
  -- 普通索引：常用查询条件
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_type` (`order_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  -- 联合索引：按用户+状态查询（高频场景）
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电订单表';

-- 系统日志表（system_log）
CREATE TABLE IF NOT EXISTS system_log (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
    `user_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '操作用户 ID（关联用户表 id）',
    `admin_id` INT UNSIGNED NULL DEFAULT NULL COMMENT '操作管理员 ID（关联管理员表 id）',
    `module` VARCHAR(50) NOT NULL COMMENT '操作模块（非空）',
    `action` VARCHAR(100) NOT NULL COMMENT '操作行为（非空）',
    `method` VARCHAR(10) NULL DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `url` VARCHAR(255) NULL DEFAULT NULL COMMENT '请求 URL',
    `params` TEXT NULL DEFAULT NULL COMMENT '请求参数',
    `result` TEXT NULL DEFAULT NULL COMMENT '返回结果',
    `ip` VARCHAR(50) NULL DEFAULT NULL COMMENT '操作 IP',
    `user_agent` VARCHAR(500) NULL DEFAULT NULL COMMENT '用户代理',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '操作状态：0 - 失败，1 - 成功（默认 1）',
    `error_msg` TEXT NULL DEFAULT NULL COMMENT '错误信息',
    `execution_time` INT UNSIGNED NULL DEFAULT NULL COMMENT '执行时长（毫秒）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
    -- 主键索引
    PRIMARY KEY (`id`),
    -- 常用查询索引
    INDEX idx_module (`module`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_admin_id (`admin_id`),
    INDEX idx_create_time (`create_time`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

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


-- 私桩动态价格表
CREATE TABLE `private_station_dynamic_price` (
  `id` INT AUTO_INCREMENT COMMENT '主键',
  `station_id` INT DEFAULT NULL COMMENT '私桩ID',
  `begin_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `price` DECIMAL(10, 2) DEFAULT NULL COMMENT '充电价格，单位：元/度',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私桩动态价格表';

-- 邀请码表 admin_invite_code
CREATE TABLE IF NOT EXISTS `admin_invite_code` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `code` VARCHAR(32) NOT NULL COMMENT '邀请码（非空）',
  `created_by` INT NOT NULL COMMENT '创建人 ID（超级管理员，非空）',
  `target_type` TINYINT NOT NULL DEFAULT 10 COMMENT '注册后的管理员类型：0:超级管理员，1：普通管理员（无法修改其他管理成员权限），2：审核员，3：客服，4：紧急响应人员，5：论坛管理员，10：未分配身份',
  `max_uses` INT NOT NULL DEFAULT 1 COMMENT '最大使用次数（默认 1）',
  `used_count` INT NOT NULL DEFAULT 0 COMMENT '已使用次数（默认 0）',
  `expire_time` DATETIME NULL COMMENT '过期时间（可为空）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0 - 有效，1 - 已禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（默认当前时间，更新时自动刷新）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请码表';

-- 帖子表（forum_post）
CREATE TABLE `forum_post` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '帖子作者id(关联用户表id)',
  `title` varchar(255) NOT NULL COMMENT '帖子标题',
  `content` text COMMENT '帖子内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数（默认0）',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏数（默认0）',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_essence` tinyint NOT NULL DEFAULT 0 COMMENT '是否精华：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`),
  -- 常用查询索引：按作者查帖子、按置顶/精华筛选、按创建时间排序
  KEY `idx_user_id` (`user_id`),
  KEY `idx_top_essence` (`is_top`, `is_essence`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆论坛-帖子表';




-- 回复表（post_reply）
CREATE TABLE `post_reply` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` int NOT NULL COMMENT '帖子ID(关联帖子表id)',
  `user_id` int NOT NULL COMMENT '回复用户ID(关联用户表id)',
  `parent_id` int DEFAULT NULL COMMENT '父回复ID，NULL表示直接回复帖子',
  `reply_to_user_id` int DEFAULT NULL COMMENT '被回复用户ID(关联用户表id)',
  `content` text NOT NULL COMMENT '回复内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0：正常，1：已删除',
  PRIMARY KEY (`id`),
  -- 核心查询索引：按帖子查所有回复、按父回复查子回复、按用户查回复
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`),
  -- 外键约束（可选，根据业务是否需要强关联）
  CONSTRAINT `fk_post_reply_post_id` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆论坛-回复表';

-- 帖子收藏表
CREATE TABLE IF NOT EXISTS post_favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    post_id INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0
);


-- 帖子点赞表
CREATE TABLE post_like (
    id INT AUTO_INCREMENT COMMENT '主键',
    user_id INT COMMENT '用户ID(关联用户表id)',
    post_id INT COMMENT '帖子ID(关联帖子表id)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- AI 对话主表
CREATE TABLE ai_conversation (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键（自增）',
    user_id INT COMMENT '用户 ID（关联 user_info 表）',
    ai_model VARCHAR(50) COMMENT '使用的 AI 模型名称',
    session_id VARCHAR(100) COMMENT '会话 ID（用于上下文管理）',
    session_context TEXT COMMENT '会话上下文信息（JSON 格式）',
    status TINYINT COMMENT '会话状态：0-进行中，1-已结束，2-已中断',
    start_time DATETIME COMMENT '会话开始时间',
    end_time DATETIME COMMENT '会话结束时间',
    last_interaction_time DATETIME COMMENT '最后交互时间',
    total_messages INT COMMENT '消息总数',
    is_deleted TINYINT COMMENT '逻辑删除标识',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话主表';

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

-- AI 决策记录表
CREATE TABLE `ai_decision_record` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `decision_no` VARCHAR(32) NOT NULL COMMENT '决策编号',
    `decision_type` TINYINT NOT NULL COMMENT '决策类型：0-审核建议，1-异常预警，2-资源调度，3-风险控制，4-运营优化',
    `trigger_type` TINYINT NOT NULL DEFAULT 0 COMMENT '触发类型：0-定时任务，1-事件触发，2-手动触发',
    `business_type` VARCHAR(50) DEFAULT NULL COMMENT '关联业务类型',
    `business_id` INT DEFAULT NULL COMMENT '关联业务ID',
    `input_data` JSON COMMENT '输入数据（AI分析的原始数据）',
    `ai_analysis` TEXT COMMENT 'AI分析结果',
    `decision_result` JSON COMMENT '决策结果（结构化）',
    `suggested_actions` JSON COMMENT '建议执行的动作列表',
    `confidence` DECIMAL(5,2) DEFAULT NULL COMMENT 'AI置信度（0-100）',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级：0-低，1-普通，2-高，3-紧急',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-已采纳，2-已忽略，3-部分采纳，4-自动执行中，5-执行完成，6-执行失败',
    `auto_execute` TINYINT DEFAULT 0 COMMENT '是否自动执行：0-否，1-是',
    `execute_result` JSON COMMENT '执行结果',
    `execute_time` DATETIME DEFAULT NULL COMMENT '执行时间',
    `handled_by` INT DEFAULT NULL COMMENT '处理人ID（管理员）',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `handle_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_decision_no` (`decision_no`),
    KEY `idx_type_status` (`decision_type`, `status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_business` (`business_type`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI决策记录表';


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


-- AI 对话消息表
CREATE TABLE ai_message (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键（自增）',
    conversation_id INT COMMENT '会话 ID（关联 ai_conversation 表）',
    sender_type TINYINT COMMENT '发送者类型：0-用户，1-AI',
    message_type TINYINT COMMENT '消息类型：0-文本，1-图片，2-语音，3-系统通知',
    content TEXT COMMENT '消息内容',
    response_time INT COMMENT 'AI 响应时间（毫秒，仅 AI 消息时填充）',
    extra_info JSON COMMENT '扩展信息（如图片 URL、语音时长等）',
    send_time DATETIME COMMENT '发送时间',
    is_deleted TINYINT COMMENT '逻辑删除标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话消息表';


-- 测试数据插入脚本

-- 插入普通用户（未认证车辆和充电桩）
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_user', '普通用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138000', 'user@test.com', 0, 0, 0);

-- 插入车辆认证用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_car_owner', '车辆认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138001', 'car_owner@test.com', 2, 0, 0);

-- 插入充电桩认证用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_station_owner', '充电桩认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138002', 'station_owner@test.com', 0, 2, 0);

-- 插入同时认证车辆和充电桩的用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_all_owner', '车辆和充电桩认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138003', 'all_owner@test.com', 2, 2, 0);

-- 插入普通管理员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_admin', '普通管理员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 1, 0);

-- 插入超级管理员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_super_admin', '超级管理员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, 0);

-- 插入审核员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_auditor', '审核员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 2, 0);

-- 插入客服人员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_cs', '客服人员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 3, 0);
