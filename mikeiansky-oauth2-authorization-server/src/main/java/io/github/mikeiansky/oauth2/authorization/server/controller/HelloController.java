package io.github.mikeiansky.oauth2.authorization.server.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@RequestMapping("hello")
@RestController
public class HelloController {

    private final RedisTemplate<String, Object> redisTemplate;

    public HelloController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("redis")
    public String redis() {
        String infoTag = "info-02";
        Object cache = redisTemplate.opsForValue().get(infoTag);
        if (cache != null) {
//            redisTemplate.opsForValue().increment(infoTag, 1);
//            return cache.toString();
        } else {
            redisTemplate.opsForValue().increment(infoTag, 1);
        }
        return "Hello Redis! ";
    }

}
