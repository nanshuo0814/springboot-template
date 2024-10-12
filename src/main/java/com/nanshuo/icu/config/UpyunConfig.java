package com.nanshuo.icu.config;

import com.upyun.RestManager;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 又拍云储存配置
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/10/12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upyun.client")
public class UpyunConfig {

    /**
     * 储存桶名称
     */
    private String bucketName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    @Bean
    public RestManager restManager() {
        // 创建 RestManager 实例。
        return new RestManager(bucketName, userName, password);
    }

}
