package com.sunboat.common.core.utils;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonUtils {

    @Autowired
    private RedissonClient redissonClient;

    // 分布式锁（核心场景）
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    // 存值（支持对象）
    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeout, unit);
    }

    // 取值
    public <T> T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    // 其他分布式特性（如 RMap、RList 等）按需封装...
}