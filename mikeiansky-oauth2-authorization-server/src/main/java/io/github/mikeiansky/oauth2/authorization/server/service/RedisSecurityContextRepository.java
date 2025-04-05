package io.github.mikeiansky.oauth2.authorization.server.service;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.time.Duration;
import java.util.UUID;

public class RedisSecurityContextRepository implements SecurityContextRepository {

    private static final String AUTHENTICATION_TOKEN_PREFIX = "auth_token";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSecurityContextRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String generateAuthTokenKey(String key){
        return String.join(":", AUTHENTICATION_TOKEN_PREFIX, key);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        String authToken = getCookieValue(requestResponseHolder.getRequest(), AUTHENTICATION_TOKEN_PREFIX);
        if (authToken == null) {
            return null;
        }
        Object cache = redisTemplate.opsForValue().get(generateAuthTokenKey(authToken));
        if (cache == null) {
            return null;
        }
        Authentication authentication = JSON.parseObject(cache.toString(), Authentication.class);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String authToken = UUID.randomUUID().toString();
        int durationDay = 30;
        Cookie cookie = new Cookie(AUTHENTICATION_TOKEN_PREFIX, authToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(durationDay).getSeconds()); // 设置 7 天有效
        response.addCookie(cookie);
        String authentication = JSON.toJSONString(context.getAuthentication());
        redisTemplate.opsForValue().set(generateAuthTokenKey(authToken), authentication, Duration.ofDays(durationDay));
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }

}
