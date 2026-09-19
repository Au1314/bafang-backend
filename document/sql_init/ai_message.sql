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
