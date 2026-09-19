-- 帖子点赞表
CREATE TABLE post_like (
    id INT AUTO_INCREMENT COMMENT '主键',
    user_id INT COMMENT '用户ID(关联用户表id)',
    post_id INT COMMENT '帖子ID(关联帖子表id)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '0：正常，1：已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';