package com.iml1s.spring.redis.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/stringAndHash")
    @ResponseBody
    public Map<String, Object> testStringAndHash() {
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForValue().set("int_key", "1");

        // 非數值,會異常(RedisTemplate使用JDK序列化器)
//        redisTemplate.opsForValue().increment("int_key");

        stringRedisTemplate.opsForValue().set("int_key_s", "1");
        stringRedisTemplate.opsForValue().increment("int_key_s");
        stringRedisTemplate.opsForValue().decrement("int_key_s");

        // 使用底層Jedis連接
//        Jedis jedis = (Jedis) stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();
//        jedis.decr("int_key_s");

        Map<String, String> hash = new HashMap<>();
        hash.put("field1", "value1");
        hash.put("field2", "value2");
        stringRedisTemplate.opsForHash().putAll("hash", hash);
        stringRedisTemplate.opsForHash().put("hash", "field3", "value3");

        // 綁定hash的key
        BoundHashOperations operations = stringRedisTemplate.boundHashOps("hash");
        operations.delete("field1", "field2");
        operations.put("field4", "value4");

        HashMap map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }


    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> testList() {
        stringRedisTemplate.opsForList().leftPushAll(
                "list1", "v1", "v2", "v3", "v4", "v5"
        );

        stringRedisTemplate.opsForList().rightPushAll(
                "list2", "v1", "v2", "v3", "v4", "v5"
        );

        BoundListOperations listOperations = stringRedisTemplate.boundListOps("list2");

        HashMap map = new HashMap<String, Object>();
        map.put("success", true);
        return map;

    }
}
