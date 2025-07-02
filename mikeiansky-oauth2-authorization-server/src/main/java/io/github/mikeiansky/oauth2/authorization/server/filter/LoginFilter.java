package io.github.mikeiansky.oauth2.authorization.server.filter;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.common.RespResult;
import io.github.mikeiansky.oauth2.authorization.server.model.common.ResultCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author mike ian
 * @date 2025/7/2
 * @desc
 **/
@Slf4j
public class LoginFilter extends OncePerRequestFilter {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private final RedisTemplate<String, String> redisTemplate;

    private final SecurityContextRepository securityContextRepository;

    private SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler;

    public LoginFilter(RedisTemplate<String, String> redisTemplate,
                       SecurityContextRepository securityContextRepository,
                       RequestCache requestCache) {
        this.redisTemplate = redisTemplate;
        this.securityContextRepository = securityContextRepository;
        savedRequestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        this.savedRequestAwareAuthenticationSuccessHandler.setRequestCache(requestCache);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/passport/login") && request.getMethod().equalsIgnoreCase("POST")) {
            String mobile = request.getParameter("mobile");
            String code = request.getParameter("code");
            if (!StringUtils.hasText(mobile)
                    || !StringUtils.hasText(code)) {
                log.error("login filter request params is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.fail(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }

            // 判断是否一致
            String cacheCode = redisTemplate.opsForValue().get(RedisConfig.getLoginSmsCode("86", mobile));
            if (!Objects.equals(code, cacheCode)) {
                log.error("login filter request code is not match, code: {}, cacheCode: {}", code, cacheCode);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.fail("验证码不正确")));
                return;
            }

            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                    mobile, null, null
            );
            // 将登录成功的数据写到cookie中
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            this.securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            savedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            return; // Stop further processing of the filter chain
        }

        // Continue with the filter chain for other requests
        filterChain.doFilter(request, response);
    }

}
