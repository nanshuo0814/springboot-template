package com.xiaoyuer.springboot.constant;

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
    String EMAIL_CODE_KEY = "email_code:";

    /**
     * 邮箱验证码过期时间（s）
     */
    Integer EMAIL_CODE_TIME_OUT = 60;

    /**
     * 图片验证码key
     */
    String IMAGE_CODE_KEY = "image_code:";

    /**
     * 图片验证码过期时间（s）
     */
    Integer IMAGE_CODE_TIME_OUT = 60;
}
