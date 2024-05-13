package com.nanshuo.springboot.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.nanshuo.springboot.config.OssClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * oss 阿里云对象存储操作
 *
 * @author nanshuo
 * @date 2024/01/26 13:26:04
 */
@Component
public class OssManager {

    @Resource
    private OssClientConfig ossClientConfig;
    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return {@code PutObjectResult}
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossClientConfig.getBucket(), key,
                new File(localFilePath));
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return {@code PutObjectResult}
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossClientConfig.getBucket(), key,
                file);
        return ossClient.putObject(putObjectRequest);
    }
}
