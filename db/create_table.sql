# 数据库初始化

-- 创建库
CREATE DATABASE IF NOT EXISTS `project_db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 使用换库
USE `project_db`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户id',
    `user_account`  VARCHAR(16)                          NOT NULL COMMENT '用户账号',
    `user_password` VARCHAR(256)                         NOT NULL COMMENT '用户密码',
    `union_id`       varchar(256)                         null comment '微信开放平台id',
    `mp_open_id`      varchar(256)                         null comment '公众号openId',
    `user_name`     VARCHAR(256)                         NULL COMMENT '用户昵称',
    `user_gender`   TINYINT    DEFAULT 2                 NULL COMMENT '0-女，1-男，2-未知',
    `user_email`    VARCHAR(255)                         NULL COMMENT '邮箱',
    `user_avatar`   VARCHAR(1024)                        NULL COMMENT '用户头像',
    `user_profile`  VARCHAR(512)                         NULL COMMENT '用户简介',
    `user_role`     VARCHAR(5) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin/ban',
    `create_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     TINYINT    DEFAULT 0                 NOT NULL COMMENT '是否删除，0:默认，1:删除',
    index idx_unionId (union_id)
) COMMENT '用户' ENGINE = InnoDB
                 CHARACTER SET utf8mb4
                 COLLATE utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    thumb_num   int      default 0                 not null comment '点赞数',
    favour_num  int      default 0                 not null comment '收藏数',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_user_id (user_id)
) comment '帖子' ENGINE = InnoDB
                 CHARACTER SET utf8mb4
                 COLLATE utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_user_id (user_id)
) comment '帖子点赞' ENGINE = InnoDB
                     CHARACTER SET utf8mb4
                     COLLATE utf8mb4_unicode_ci;

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_user_id (user_id)
) comment '帖子收藏' ENGINE = InnoDB
                     CHARACTER SET utf8mb4
                     COLLATE utf8mb4_unicode_ci;
