# 数据库初始化

-- 创建库
CREATE DATABASE IF NOT EXISTS `springboot_template`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 切换库
USE `springboot_template`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户id',
    `user_account`  VARCHAR(16)                          NOT NULL COMMENT '用户账号',
    `user_password` VARCHAR(256)                         NOT NULL COMMENT '用户密码',
    `user_name`     VARCHAR(256)                         NULL COMMENT '用户昵称',
    `user_gender`   TINYINT    DEFAULT 2                 NULL COMMENT '0-女，1-男，2-未知',
    `user_email`    VARCHAR(255)                         NULL COMMENT '邮箱',
    `user_avatar`   VARCHAR(1024)                        NULL COMMENT '用户头像',
    `user_profile`  VARCHAR(512)                         NULL COMMENT '用户简介',
    `user_role`     VARCHAR(5) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin/ban',
    `create_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     TINYINT    DEFAULT 0                 NOT NULL COMMENT '是否删除，0:默认，1:删除'
) COMMENT '用户' ENGINE = InnoDB
                 CHARACTER SET utf8mb4
                 COLLATE utf8mb4_unicode_ci;

-- 插入测试数据
INSERT INTO `user` (`user_account`,
                    `user_password`,
                    `user_name`,
                    `user_gender`,
                    `user_email`,
                    `user_avatar`,
                    `user_profile`,
                    `user_role`,
                    `create_time`,
                    `update_time`,
                    `is_delete`)
VALUES ('admin',
        'aab77ea07b32db300f6b1fa6972e0210',
        'Test User 1',
        1,
        'test_user1@example.com',
        'avatar_url_1',
        'This is the profile of Test User 1',
        'admin',
        '2023-01-01 12:00:00',
        '2023-01-01 12:00:00',
        0),
       ('a888888',
        '178c3bbe877bb188827bba3bb63104e4',
        'Test User 2',
        0,
        'test_user2@example.com',
        'avatar_url_2',
        'This is the profile of Test User 2',
        'ban',
        '2023-01-02 12:00:00',
        '2023-01-02 12:00:00',
        0),
       ('777777',
        '1cc9111426e7129f65a38bd017ff6f99',
        'Test User 3',
        2,
        'test_user3@example.com',
        'avatar_url_3',
        'This is the profile of Test User 3',
        'ban',
        '2023-01-03 12:00:00',
        '2023-01-03 12:00:00',
        1);
