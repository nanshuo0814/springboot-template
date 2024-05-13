package com.nanshuo.springboot.utils.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redisson utils
 *
 * @author nanshuo
 * @date 2024/03/28 11:12:22
 */
@Component
public class RedissonUtils {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 获取锁
     *
     * @param lockName 锁定名称
     * @return {@code RLock}
     */
    public RLock getLock(String lockName) {
        return redissonClient.getLock(lockName);
    }

    /**
     * 尝试锁定
     *
     * @param lockName  锁定名称
     * @param waitTime  等待时间
     * @param leaseTime 租赁时间
     * @return boolean
     */
    public boolean tryLock(String lockName, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param lockName 锁定名称
     */
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock();
    }

}