package com.nanshuo.icu.constant;

/**
 * redis key 常量
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/03
 */
public interface RedisKeyConstant {

    /**
     * 邮箱验证码key
     */
    String EMAIL_CAPTCHA_KEY = "email_captcha_";

    /**
     * 用于邮箱重置密码的验证码凭证
     */
    String VOUCHER = "voucher:";

    /**
     * 图片验证码key
     */
    String IMAGE_CAPTCHA_KEY = "image_captcha:";

    /**
     * 用户登录状态缓存
     */
    String USER_LOGIN_STATE_CACHE = "user_login_cache:";

}
