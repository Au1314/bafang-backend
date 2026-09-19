-- 车辆论坛帖子表
CREATE TABLE IF NOT EXISTS forum_post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    title VARCHAR(255),
    content TEXT,
    images JSON,
    like_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    is_top TINYINT,
    is_essence TINYINT,
    create_time DATETIME,
    update_time DATETIME,
    is_deleted TINYINT
);