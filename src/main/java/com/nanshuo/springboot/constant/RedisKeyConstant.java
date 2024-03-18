package com.nanshuo.springboot.constant;

/**
 * redis密钥常量
 *
 * @author 小鱼儿
 * @date 2024/01/03 19:26:32
 */
public interface RedisKeyConstant {

    /**
     * 邮箱验证码key
     */
    String EMAIL_CAPTCHA_KEY = "email_captcha:";

    /**
     * 图片验证码key
     */
    String IMAGE_CAPTCHA_KEY = "image_captcha:";

    /**
     * 图片验证码过期时间（秒）
     */
    Integer IMAGE_CAPTCHA_TIME_OUT = 180;

    /**
     * 用户登录状态缓存
     */
    String USER_LOGIN_STATE_CACHE = "user_login_cache:";
}
