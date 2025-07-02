package io.github.mikeiansky.oauth2.authorization.server.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.common.RespResult;
import io.github.mikeiansky.oauth2.authorization.server.model.common.ResultCode;
import io.github.mikeiansky.oauth2.authorization.server.model.dto.SendSmsCodeDTO;
import io.github.mikeiansky.oauth2.authorization.server.model.vo.SendSmsCodeVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 *
 * @author mike ian
 * @date 2025/7/1
 * @desc
 **/
@Slf4j
public class SendCodeFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    public SendCodeFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/passport/send-code") && request.getMethod().equals("POST")) {

            SendSmsCodeDTO sendCodeDTO = JSON.parseObject(IoUtil.read(request.getReader()), SendSmsCodeDTO.class);
            if (sendCodeDTO == null) {
                log.error("send code filter request params is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.of(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }

            if (!StringUtils.hasText(sendCodeDTO.getMobile())) {
                log.error("send code filter request mobile is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.of(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }

            if (!StringUtils.hasText(sendCodeDTO.getChannel())) {
                log.error("send code filter request channel is null");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSON.toJSONString(RespResult.of(ResultCode.INVALID_REQUEST_PARAM)));
                return; // Stop further processing of the filter chain
            }

            // TODO 真实发送code请求参数
            String code = String.format("%04d", RandomUtil.randomInt(0, 9999));
            redisTemplate.opsForValue().set(RedisConfig.getLoginSmsCode("86", sendCodeDTO.getMobile()), code, Duration.ofMinutes(5));

            log.info("send code filter code: {}", code);


            SendSmsCodeVO sendSmsCodeVO = new SendSmsCodeVO();
            sendSmsCodeVO.setCode(code);
            String result = JSON.toJSONString(sendSmsCodeVO);

            // If it is, set the response status to 200 OK
            response.setStatus(HttpServletResponse.SC_OK);
            // Optionally, you can also write a message to the response body
            response.getWriter().write(result);
            return; // Stop further processing of the filter chain
        }

        filterChain.doFilter(request, response);
    }

}
