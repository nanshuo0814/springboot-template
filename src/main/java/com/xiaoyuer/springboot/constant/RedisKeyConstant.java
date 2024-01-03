package com.xiaoyuer.springboot.constant;

/**
 * redis密钥常量
 *
 * @author 小鱼儿
 * @date 2024/01/03 19:26:32
 */
public interface RedisKeyConstant {

    /**
     * 验证码前缀
     */
    String YZM_PRE = "yzm:";
    /**
     * 验证码过期时间（s）
     */
    Integer YZM_TIME_OUT = 60;
}
