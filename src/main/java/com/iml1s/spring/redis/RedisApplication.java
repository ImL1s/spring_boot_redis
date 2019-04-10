package com.iml1s.spring.redis;

import com.iml1s.spring.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.applet.AppletContext;

@SpringBootApplication
public class RedisApplication {

    @Autowired
    private RedisTemplate redisTemplate = null;

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);

//        ApplicationContext context = new AnnotationConfigApplicationContext(RedisConfig.class);
//
//        // 使用Redis Template
//        useRedisTemplate(context);
//
//        // 直接使Redis Connection factory
//        useRedisConnectionFactory(context);
//
//        // 使用Redis Callback節省連接(較底層,不好用)
//        useRedisCallback(context);
//
//        // 使用Session Callback節省連接(推薦使用)
//        useSessionCallback(context);

    }


    @PostConstruct
    public void init() {
        initRedisTemplate();
    }

    private void initRedisTemplate() {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.opsForValue().set("initRedisTemplate", "OK".getBytes());
    }

    private static void useSessionCallback(ApplicationContext context) {
        RedisTemplate redisTemplate = context.getBean(RedisTemplate.class);
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForValue().set("sessionCallback", "nice");
                redisOperations.opsForHash().put("sessionCallbackH", "sessionCallbackK", "sessionCallbackV");
                return null;
            }
        });
    }

    private static void useRedisCallback(ApplicationContext context) {
        RedisTemplate redisTemplate = context.getBean(RedisTemplate.class);
        redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.set("redisCallback".getBytes(), "disagreeables".getBytes());
            redisConnection.hSet("redisCallbackH".getBytes(), "disagreeables".getBytes(), "disagreeablesV".getBytes());
            redisConnection.close();
            return null;
        });
    }

    private static void useRedisConnectionFactory(ApplicationContext context) {
        RedisConnectionFactory factory = context.getBean(RedisConnectionFactory.class);
        RedisConnection connection = factory.getConnection();
        connection.set("hello".getBytes(), "world".getBytes());
        connection.close();
    }

    private static void useRedisTemplate(ApplicationContext context) {
        RedisTemplate<Object, Object> redisTemplate = context.getBean(RedisTemplate.class);
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForHash().put("hash", "field", "hvalue");
    }

}
