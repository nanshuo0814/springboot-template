package com.nanshuo.springboot.utils;

import com.nanshuo.springboot.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class RedisUtilsTest {

    @Resource
    private RedisUtils redisUtils;

    @Test
    void globalUniqueKey() {
        log.info("globalUniqueKey:{}", redisUtils.globalUniqueKey("user1"));
        redisUtils.del("icr");
        System.out.println(redisUtils.get("user"));
    }
}