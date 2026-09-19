-- 回复表（对帖子的回复，以及对回复的回复）
CREATE TABLE IF NOT EXISTS post_reply (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT,
    user_id INT,
    parent_id INT DEFAULT NULL,
    reply_to_user_id INT DEFAULT NULL,
    content TEXT,
    like_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0
);