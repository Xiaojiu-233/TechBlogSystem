package com.blog.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    //redis公共的互斥锁资源
    public static Lock reenLock = new ReentrantLock();

    //调用数据库0的redisTemplate（登录凭证缓存）
    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(
                factory.getStandaloneConfiguration(),factory.getClientConfiguration());
        lettuce.setDatabase(0);
        lettuce.afterPropertiesSet();
        redisTemplate.setConnectionFactory(lettuce);
        return redisTemplate;
    }

    //调用数据库1的redisTemplate（点赞缓存）
    @Bean("redisTemplate_1")
    public RedisTemplate<Object, Object> redisTemplate_1(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(
                factory.getStandaloneConfiguration(),factory.getClientConfiguration());
        lettuce.setDatabase(1);
        lettuce.afterPropertiesSet();
        redisTemplate.setConnectionFactory(lettuce);
        return redisTemplate;
    }

    //调用数据库2的redisTemplate（访问数据缓存）
    @Bean("redisTemplate_2")
    public RedisTemplate<Object, Object> redisTemplate_2(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(
                factory.getStandaloneConfiguration(),factory.getClientConfiguration());
        lettuce.setDatabase(2);
        lettuce.afterPropertiesSet();
        redisTemplate.setConnectionFactory(lettuce);
        return redisTemplate;
    }

    //调用数据库3的redisTemplate（登陆失败次数缓存）
    @Bean("redisTemplate_3")
    public RedisTemplate<Object, Object> redisTemplate_3(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(
                factory.getStandaloneConfiguration(),factory.getClientConfiguration());
        lettuce.setDatabase(3);
        lettuce.afterPropertiesSet();
        redisTemplate.setConnectionFactory(lettuce);
        return redisTemplate;
    }

    //调用数据库4的redisTemplate（点赞通知缓存）
    @Bean("redisTemplate_4")
    public RedisTemplate<Object, Object> redisTemplate_4(LettuceConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(
                factory.getStandaloneConfiguration(),factory.getClientConfiguration());
        lettuce.setDatabase(4);
        lettuce.afterPropertiesSet();
        redisTemplate.setConnectionFactory(lettuce);
        return redisTemplate;
    }

}