package io.github.mikeiansky.oauth2.authorization.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 * @author mike ian
 * @date 2025/7/1
 * @desc
 **/
@Slf4j
public class SendCodeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("send code filter request URI: {}", requestURI);
        if (requestURI.equals("/passport/send-code")) {
            // If it is, set the response status to 200 OK
            response.setStatus(HttpServletResponse.SC_OK);
            // Optionally, you can also write a message to the response body
            response.getWriter().write("Send Code");
            return; // Stop further processing of the filter chain
        }

        filterChain.doFilter(request, response);
    }

}
