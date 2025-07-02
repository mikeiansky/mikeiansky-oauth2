package io.github.mikeiansky.oauth2.authorization.server.filter;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.common.RespResult;
import io.github.mikeiansky.oauth2.authorization.server.model.common.ResultCode;
import io.github.mikeiansky.oauth2.authorization.server.model.dto.LoginDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, String> redisTemplate;

    public LoginFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/passport/login") && request.getMethod().equalsIgnoreCase("POST")) {

            response.setContentType("application/json; charset=UTF-8"); // 可选：同时设置 Content-Type
            LoginDTO loginDTO = JSON.parseObject(IoUtil.read(request.getReader()), LoginDTO.class);
            if (loginDTO == null) {
                log.error("login filter request params is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.fail(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }
            if (!StringUtils.hasText(loginDTO.getChannel())
                    || !StringUtils.hasText(loginDTO.getCode())
                    || !StringUtils.hasText(loginDTO.getMobile())) {
                log.error("login filter request params is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.fail(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }

            // 判断是否一致
            String code = redisTemplate.opsForValue().get(RedisConfig.getLoginSmsCode("86", loginDTO.getMobile()));
            if (!Objects.equals(code, loginDTO.getCode())) {
                log.error("login filter request code is not match, code: {}, request: {}", code, loginDTO);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.fail("验证码不正确")));
                return;
            }

            // If it is, set the response status to 200 OK
            response.setStatus(HttpServletResponse.SC_OK);
            // Optionally, you can also write a message to the response body
            response.getWriter().write("Login success");
            return; // Stop further processing of the filter chain
        }

        // Continue with the filter chain for other requests
        filterChain.doFilter(request, response);
    }

}
