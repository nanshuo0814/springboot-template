# 数据库初始化

-- 创建库
create database if not exists project_db
    character set utf8mb4
    collate utf8mb4_unicode_ci;

-- 使用换库
use project_db;

-- 用户表
create table if not exists user
(
    id            bigint unsigned auto_increment primary key comment '用户id',
    user_account  varchar(16)                          not null comment '用户账号',
    user_password varchar(256)                         not null comment '用户密码',
    union_id      varchar(256)                         null comment '微信开放平台id',
    mp_open_id    varchar(256)                         null comment '公众号openid',
    user_name     varchar(256)                         null comment '用户昵称',
    user_gender   tinyint    default 2                 null comment '0-女，1-男，2-未知',
    user_email    varchar(255)                         null comment '邮箱',
    user_avatar   varchar(1024)                        null comment '用户头像',
    user_profile  varchar(512)                         null comment '用户简介',
    user_role     varchar(5) default 'user'            not null comment '用户角色：user/admin/ban',
    create_by    bigint unsigned                      not null comment '创建人的id',
    update_by    bigint unsigned                      not null comment '更新人的id',
    create_time   datetime   default current_timestamp not null comment '创建时间',
    update_time   datetime   default current_timestamp on update current_timestamp comment '更新时间',
    is_delete     tinyint    default 0                 not null comment '是否删除，0:默认，1:删除',
    index idx_union_id (union_id),
    unique index uidx_user_account (user_account),
    unique index uidex_user_email (user_email)
) comment '用户表' engine = innodb
                   character set utf8mb4
                   collate utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key comment '帖子id',
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    praise_num   int      default 0                 not null comment '点赞数',
    collect_num  int      default 0                 not null comment '收藏数',
    created_by  bigint unsigned                    not null comment '创建帖子用户的id',
    updated_by  bigint unsigned                    not null comment '更新帖子用户的id',
    create_time datetime default current_timestamp not null comment '创建时间',
    update_time datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_created_by (created_by)
) comment '帖子表' engine = innodb
                   character set utf8mb4
                   collate utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_praise
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    created_by  bigint unsigned                    not null comment '点赞帖子用户的id',
    updated_by  bigint unsigned                    not null comment '点赞帖子用户的id',
    create_time datetime default current_timestamp not null comment '创建时间',
    update_time datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    index idx_post_id (post_id),
    index idx_created_by (created_by)
) comment '帖子点赞表' engine = innodb
                       character set utf8mb4
                       collate utf8mb4_unicode_ci;

-- 帖子收藏表（硬删除）
create table if not exists post_collect
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子的id',
    created_by  bigint unsigned                    not null comment '创建帖子收藏的用户id',
    updated_by  bigint unsigned                    not null comment '更新帖子收藏的用户id',
    create_time datetime default current_timestamp not null comment '创建时间',
    update_time datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    index idx_post_id (post_id),
    index idx_created_by (created_by)
) comment '帖子收藏表' engine = innodb
                       character set utf8mb4
                       collate utf8mb4_unicode_ci;
