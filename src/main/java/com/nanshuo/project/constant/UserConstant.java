package com.nanshuo.project.constant;

/**
 * 用户常量
 *
 * @author nanshuo
 * @date 2024/01/03 19:33:58
 */
public interface UserConstant {


    /**
     * 盐值，混淆密码
     */
    String SALT = "ydg0814";

    /**
     * 用户登录session键
     */
    String USER_LOGIN_STATE = "user_login";


    /**
     * 默认角色
     */
    String USER_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 默认用户账号密码
     */
    String DEFAULT_USER_PASSWORD = "user123";

    /**
     * 默认用户昵称
     */
    String DEFAULT_USER_NAME = "user";

    /**
     * 默认用户简介
     */
    String DEFAULT_USER_PROFILE = "本人很懒,什么也没有留下ya";

    /**
     * 默认用户头像
     */
    String DEFAULT_USER_AVATAR = "https://img.ydg.icu/nanshuo.png";

    /**
     * 默认用户性别(0:女 1:男 2:未知)
     */
    Integer DEFAULT_USER_GENDER = 2;

    /**
     * 图片验证码过期时间（秒）
     */
    Integer IMAGE_CAPTCHA_TIME_OUT = 120;

    /**
     * 用户Redis缓存过期时间(小时)
     */
    Integer USER_CACHE_TIME_OUT = 1;

    String IMAGE_CAPTCHA_COOKIE_NAME = "image_captcha";
}
