package io.github.mikeiansky.oauth2.authorization.server.config;

import io.github.mikeiansky.oauth2.authorization.server.service.RedisSecurityContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, RedisTemplate<String, String> redisTemplate) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        httpSecurity
                .csrf(csrf -> csrf.disable())
//                .csrf(csrf -> csrf.ignoringRequestMatchers(
//                        new AntPathRequestMatcher("/oauth2/**"),   // 授权端点
//                        new AntPathRequestMatcher("/login"),       // 登录页面
//                        new AntPathRequestMatcher("/logout")       // 登出端点
//                ))
                .with(authorizationServerConfigurer, authorizationServer -> {
                    authorizationServer.oidc(Customizer.withDefaults());
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/passport/**",
                            "/login").permitAll();
                })
                .requestCache(requestCache -> {
                    requestCache.requestCache(new CookieRequestCache());
                })
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(new RedisSecurityContextRepository(redisTemplate));
                })
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
//                .formLogin(Customizer.withDefaults())
//                .formLogin(formLogin -> formLogin.disable())
                .rememberMe(remember -> {
                    remember.key("remember-me");
                })
                .exceptionHandling(exception -> {
                    log.info("exceptionHandling ::: exp {}", exception);
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        log.info("authenticationEntryPoint ::::::  + url : {}", request.getRequestURI());
                        authException.printStackTrace();
                        // 如果是移动端这返回错误
                        response.sendRedirect(request.getContextPath() + "/passport/login");
                    });
                })
//                .addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new LoginPageFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
        return httpSecurity.build();
    }

}
