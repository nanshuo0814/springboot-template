package com.xiaoyuer.springboot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * redis配置
 *
 * @author 小鱼儿
 * @date 2024/01/03 20:24:33
 */
@EnableCaching
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate并返回
     *
     * @param factory Redis连接工厂
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 实例化RedisTemplate对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 配置RedisTemplate
        configureRedisTemplate(redisTemplate, factory);

        // 返回配置好的RedisTemplate对象
        return redisTemplate;

    }


    /**
     * 配置Redis缓存管理器
     *
     * @param factory Redis连接工厂
     * @return Redis缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 创建Redis字符串序列化器
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        // 创建Jackson 2 JSON Redis对象序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = createJackson2JsonRedisSerializer();

        // 配置Redis缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        // 构建Redis缓存管理器
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }


    /**
     * 配置RedisTemplate
     *
     * @param redisTemplate RedisTemplate对象
     * @param factory Redis连接工厂对象
     */
    private void configureRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(factory);
        // 设置Redis值的序列化方式
        redisTemplate.setValueSerializer(createJackson2JsonRedisSerializer());
        // 设置Redis连接工厂属性后缀
        redisTemplate.afterPropertiesSet();
        // 设置Redis键的序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());

    }


    /**
     * 创建jackson2 json redis序列化程序
     *
     * @return {@code Jackson2JsonRedisSerializer<Object>}
     */
    private Jackson2JsonRedisSerializer<Object> createJackson2JsonRedisSerializer() {
        // 初始化一个Jackson2JsonRedisSerializer对象，并指定序列化类型为Object.class
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 创建一个ObjectMapper对象
        ObjectMapper om = new ObjectMapper();
        // 设置ObjectMapper的可见性属性为ALL，JsonAutoDetect的可见性属性为ANY
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 激活ObjectMapper的默认类型设置，使用LaissezFaireSubTypeValidator.instance作为验证器，DefaultTyping.NON_FINAL作为类型化方式
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 为 Long 类型配置了一个 ToStringSerializer，避免了Long精度丢失问题
        om.registerModule(new SimpleModule()
                .addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance));
        // 将创建的ObjectMapper对象设置为Jackson2JsonRedisSerializer的对象
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 返回Jackson2JsonRedisSerializer对象
        return jackson2JsonRedisSerializer;
    }

}
