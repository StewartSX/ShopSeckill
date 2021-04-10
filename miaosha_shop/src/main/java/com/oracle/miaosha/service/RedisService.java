package com.oracle.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    @Qualifier("redisTemplate")
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * redis的set方法
     * @param prefix key的前缀
     * @param key key
     * @param value value
     * @param timeout 超时时间
     */
    public void set(String prefix, String key, Object value, int timeout){
        redisTemplate.opsForValue().set(prefix + ":" + key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * redis的set方法(无过期时间)
     * @param prefix key的前缀
     * @param key key
     * @param value value
     */
    public void set(String prefix, String key, Object value){
        redisTemplate.opsForValue().set(prefix + ":" + key, value);
    }

    /**
     * redis的setIfAbsent()方法
     * @param prefix key的前缀
     * @param key key
     * @param value value
     * @return 如果存在相同的key则返回false，如果不存在则返回true并且将数据存入redis
     */
    public boolean setIfAbsent(String prefix, String key, Object value){
        return redisTemplate.opsForValue().setIfAbsent(prefix + ":" + key, value);
    }

    /**
     * redis的get方法
     * @param key key
     * @param clazz 要取出value的类型
     * @param <T> 泛型T
     * @return value
     */
    public <T> T get(String key, Class<T> clazz){
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * redis的get方法，带key的前缀
     * @param prefix key的前缀
     * @param key key
     * @param clazz 要取出value的类型
     * @param <T> 泛型T
     * @return value
     */
    public <T> T get(String prefix, String key, Class<T> clazz){
        return (T) redisTemplate.opsForValue().get(prefix + ":" + key);
    }

    /**
     * 向redis中存储String类型的数据
     * @param prefix key的前缀
     * @param key key
     * @param value value
     * @param timeout 超时时间
     */
    public void setString(String prefix, String key, String value, int timeout){
        stringRedisTemplate.opsForValue().set(prefix + ":" + key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 从redis中获取String类型的数据
     * @param prefix key的前缀
     * @param key key
     * @return value
     */
    public String getString(String prefix, String key){
        return stringRedisTemplate.opsForValue().get(prefix + ":" + key);
    }

    /**
     * 对当前的key的value做一个-1的操作
     * @param prefix key的前缀
     * @param key key
     * @return ?
     */
    public long decrement(String prefix, String key){
        return redisTemplate.opsForValue().decrement(prefix + ":" + key);
    }

    /**
     * 对当前的key的value做一个+1的操作
     * @param prefix key的前缀
     * @param key key
     * @return ?
     */
    public long increment(String prefix, String key){
        return redisTemplate.opsForValue().increment(prefix + ":" + key);
    }

    /**
     * 判断redis中是否存在某个key
     * @param prefix key的前缀
     * @param key key
     * @return true & false
     */
    public boolean hasKey(String prefix, String key){
        return redisTemplate.hasKey(prefix + ":" + key);
    }

}
