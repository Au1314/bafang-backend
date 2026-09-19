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
