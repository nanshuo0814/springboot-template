package com.nanshuo.springboot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * redisson配置
 *
 * @author nanshuo
 * @date 2024/03/28 10:14:56
 */
// todo 取消注释开启redisson
//@Configuration
public class RedissonConfig {

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                .setDatabase(2);
        return Redisson.create(config);
    }
}
