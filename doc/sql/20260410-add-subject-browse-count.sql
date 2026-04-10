-- 为 subject_info 表新增浏览次数字段
-- 执行库：当前连接库（DATABASE()）

SET @ddl := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'subject_info'
              AND COLUMN_NAME = 'browse_count'
        ),
        "SELECT 'browse_count column already exists'",
        "ALTER TABLE `subject_info` ADD COLUMN `browse_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '浏览次数' AFTER `subject_parse`"
    )
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
