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
    `union_id`      VARCHAR(256)                         NULL COMMENT '微信开放平台id',
    `mp_open_id`    VARCHAR(256)                         NULL COMMENT '公众号openId',
    `user_name`     VARCHAR(256)                         NULL COMMENT '用户昵称',
    `user_gender`   TINYINT    DEFAULT 2                 NULL COMMENT '0-女，1-男，2-未知',
    `user_email`    VARCHAR(255)                         NULL COMMENT '邮箱',
    `user_avatar`   VARCHAR(1024)                        NULL COMMENT '用户头像',
    `user_profile`  VARCHAR(512)                         NULL COMMENT '用户简介',
    `user_role`     VARCHAR(5) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin/ban',
    `create_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     TINYINT    DEFAULT 0                 NOT NULL COMMENT '是否删除，0:默认，1:删除',
    INDEX `idx_union_id` (`union_id`)
) COMMENT '用户' ENGINE = InnoDB
                 CHARACTER SET utf8mb4
                 COLLATE utf8mb4_unicode_ci;

-- 插入测试数据
INSERT INTO `user` (`user_account`,
                    `user_password`,
                    `union_id`,
                    `mp_open_id`,
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
        'hashed_password1',
        'wx_union_id_1',
        'mp_open_id_1',
        'Test User 1',
        1,
        'test_user1@example.com',
        'avatar_url_1',
        'This is the profile of Test User 1',
        'user',
        '2023-01-01 12:00:00',
        '2023-01-01 12:00:00',
        0),
       ('user123',
        'hashed_password2',
        'wx_union_id_2',
        'mp_open_id_2',
        'Test User 2',
        0,
        'test_user2@example.com',
        'avatar_url_2',
        'This is the profile of Test User 2',
        'admin',
        '2023-01-02 12:00:00',
        '2023-01-02 12:00:00',
        0),
       ('888888',
        'hashed_password3',
        NULL,
        NULL,
        'Test User 3',
        2,
        'test_user3@example.com',
        'avatar_url_3',
        'This is the profile of Test User 3',
        'ban',
        '2023-01-03 12:00:00',
        '2023-01-03 12:00:00',
        0);
