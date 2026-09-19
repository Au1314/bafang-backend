-- 帖子收藏表
CREATE TABLE IF NOT EXISTS post_favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    post_id INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0
);