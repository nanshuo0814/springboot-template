package com.nanshuo.project.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云对象存储客户端
 *
 * @author nanshuo
 * @date 2024/01/26 13:21:40
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oss.client")
public class OssClientConfig {

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 区域
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;

    @Bean
    public OSS ossClient() {
        // 创建OSSClient实例。
        return new OSSClientBuilder().build(region, accessKey, secretKey);
    }
}