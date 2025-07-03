package io.github.mikeiansky.oauth2.authorization.server.service;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class RedisSecurityContextRepository implements SecurityContextRepository {

    private static final String LOGIN_TOKEN_COOKIE_KEY = "login_token";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisSecurityContextRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        // 登录的token ，如果用header里面的话，需要使用X-Login-Token，避免使用OAuth2规范的Authorization的Basic和Bearer token
        Cookie authTokenCookie = WebUtils.getCookie(requestResponseHolder.getRequest(), LOGIN_TOKEN_COOKIE_KEY);
        if (authTokenCookie == null) {
            return null;
        }
        Object cache = redisTemplate.opsForValue().get(RedisConfig.getLoginTokenKey(authTokenCookie.getValue()));
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

        log.info("save authentication class : {}, value : {} ", authentication.getClass().getName(),
                JSON.toJSONString(authentication));

        String userId = authentication.getPrincipal().toString();
        String authenticationJson = JSON.toJSONString(context.getAuthentication());
        String loginUserKey = RedisConfig.getLoginUserKey(userId, "pc");
        String loginTokenCache = redisTemplate.opsForValue().get(loginUserKey);
        if (loginTokenCache != null) {
            // 删除重复的旧的token，同客户端需要互踢下线
            redisTemplate.delete(RedisConfig.getLoginTokenKey(loginTokenCache));
        }
        redisTemplate.opsForValue().set(loginUserKey, loginToken, Duration.ofDays(durationDay));
        redisTemplate.opsForValue().set(RedisConfig.getLoginTokenKey(loginToken), authenticationJson, Duration.ofDays(durationDay));
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }

}
