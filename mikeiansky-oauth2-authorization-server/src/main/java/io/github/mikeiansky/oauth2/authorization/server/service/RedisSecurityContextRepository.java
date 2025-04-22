package io.github.mikeiansky.oauth2.authorization.server.service;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.UUID;

public class RedisSecurityContextRepository implements SecurityContextRepository {

    private static final String LOGIN_TOKEN_COOKIE_KEY = "login_token";

    private static final String SERVER_REDIS_KEY_PREFIX = "oauth2:server";

    public static final String LOGIN_TOKEN_REDIS_KEY = "login:token";

    public static final String LOGIN_USER_REDIS_KEY = "login:user";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSecurityContextRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static String getLoginTokenKey(String key) {
        return String.join(":", SERVER_REDIS_KEY_PREFIX, LOGIN_TOKEN_REDIS_KEY, key);
    }

    public static String getLoginUserRedisKey(String userId, String channel) {
        return String.join(":", SERVER_REDIS_KEY_PREFIX, LOGIN_USER_REDIS_KEY, String.valueOf(userId), channel);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        // 登录的token ，如果用header里面的话，需要使用X-Login-Token，避免使用OAuth2规范的Authorization的Basic和Bearer token
        Cookie authTokenCookie = WebUtils.getCookie(requestResponseHolder.getRequest(), LOGIN_TOKEN_COOKIE_KEY);
        if (authTokenCookie == null) {
            return null;
        }
        Object cache = redisTemplate.opsForValue().get(getLoginTokenKey(authTokenCookie.getValue()));
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
        String loginToken = UUID.randomUUID().toString();
        // 有效的时间
        int durationDay = 7;
        Cookie cookie = new Cookie(LOGIN_TOKEN_COOKIE_KEY, loginToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(durationDay).getSeconds());
        response.addCookie(cookie);
        Authentication authentication = context.getAuthentication();
        String userId = null;
        if (authentication.getPrincipal() instanceof User user) {
            userId = user.getUsername();
        }
        String authenticationJson = JSON.toJSONString(context.getAuthentication());
        String loginUserKey = getLoginUserRedisKey(userId, "pc");
        Object loginTokenCache = redisTemplate.opsForValue().get(loginUserKey);
        if (loginTokenCache != null) {
            redisTemplate.delete(getLoginTokenKey(loginTokenCache.toString()));
        }
        redisTemplate.opsForValue().set(loginUserKey, loginToken, Duration.ofDays(durationDay));
        redisTemplate.opsForValue().set(getLoginTokenKey(loginToken), authenticationJson, Duration.ofDays(durationDay));
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }

}
