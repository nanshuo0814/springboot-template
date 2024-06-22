package com.nanshuo.project.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * captcha配置
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/28 19:17:25
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
     * 图片验证码是否开启
     */
    private boolean imageEnabled;

}
