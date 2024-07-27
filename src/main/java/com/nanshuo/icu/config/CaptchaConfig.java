package com.nanshuo.icu.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 各种验证码配置类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaConfig {

    /**
     * 邮箱验证码是否开启
     */
    private boolean emailEnabled;

    /**
     * 登录图片验证码是否开启
     */
    private boolean loginImageEnabled;


    /**
     * 注册图片验证码是否开启
     */
    private boolean registerImageEnabled;

}
