package com.jwt.freecloud.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author：揭文滔
 * @since：2023/3/1
 */
@Component
public class RedisUtil {

    /**
     * 1.使用StringRedisTemplate
     * 2.写入Redis时，手动把对象序列化为json格式字符串
     *   读取Redis时，手动把读取到的JSON反序列化成对象
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setStringWithExpire(String key, String value, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setExpire(String key, Long time, TimeUnit timeUnit) {
        if (time > 0 ) {
            redisTemplate.expire(key, time, timeUnit);
        }
    }

    public boolean deleteString(String key) {
        return redisTemplate.delete(key);
    }

    public BoundHashOperations<String, Object, Object> getHash(String key) {
        return redisTemplate.boundHashOps(key);
    }
}
