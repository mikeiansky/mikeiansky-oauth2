package io.github.mikeiansky.oauth2.authorization.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("login page filter : {}", request.getRequestURI());
        // Check if the request is for the login page
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/passport/login") && request.getMethod().equalsIgnoreCase("GET")) {
            // If it is, set the response status to 200 OK
            response.setStatus(HttpServletResponse.SC_OK);
            // Optionally, you can also write a message to the response body
            response.getWriter().write("Login Page");
            return; // Stop further processing of the filter chain
        }

        // Continue with the filter chain for other requests
        filterChain.doFilter(request, response);
    }

}
