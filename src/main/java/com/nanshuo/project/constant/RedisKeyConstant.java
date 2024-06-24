package com.nanshuo.project.constant;

/**
 * redis密钥常量
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/03 19:26:32
 */
public interface RedisKeyConstant {

    /**
     * 邮箱验证码key
     */
    String EMAIL_CAPTCHA_KEY = "email_captcha_";

    /**
     * 图片验证码key
     */
    String IMAGE_CAPTCHA_KEY = "image_captcha:";

    /**
     * 用户登录状态缓存
     */
    String USER_LOGIN_STATE_CACHE = "user_login_cache:";

}
