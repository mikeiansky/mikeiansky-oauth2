package io.github.mikeiansky.oauth2.authorization.server.filter;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author mike ian
 * @date 2025/7/1
 * @desc
 **/
@Slf4j
public class LoginPageFilter extends OncePerRequestFilter {

    private final TemplateEngine templateEngine;

    public LoginPageFilter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    private String generateLoginPageHtml(HttpServletRequest request, boolean loginError, boolean logoutSuccess) {
        Context context = new Context();
        context.setVariable("message", "Hello from Thymeleaf Test!");
        context.setVariable("now", java.time.LocalDateTime.now());
        JSONObject vv = new JSONObject();
        vv.put("parameterName", "parameterName");
        vv.put("token", "token");
        context.setVariable("_csrf", vv);

        // 2. 渲染模板（假设模板文件名为 test-page.html）
        String renderedHtml = templateEngine.process("login", context);

        return renderedHtml;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Check if the request is for the login page
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/passport/login") && request.getMethod().equalsIgnoreCase("GET")) {

            String loginPageHtml = generateLoginPageHtml(request, false, false);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=UTF-8");
            response.setContentLength(loginPageHtml.getBytes(StandardCharsets.UTF_8).length);
            response.getWriter().write(loginPageHtml);
            return; // Stop further processing of the filter chain
        }

        // Continue with the filter chain for other requests
        filterChain.doFilter(request, response);
    }

}
