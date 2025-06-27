package io.github.mikeiansky.oauth2.authorization.server.config;

import io.github.mikeiansky.oauth2.authorization.server.filter.LoginFilter;
import io.github.mikeiansky.oauth2.authorization.server.service.RedisSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, RedisTemplate<String, String> redisTemplate) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        httpSecurity
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/oauth2/**"),   // 授权端点
                        new AntPathRequestMatcher("/login"),       // 登录页面
                        new AntPathRequestMatcher("/logout")       // 登出端点
                ))
                .with(authorizationServerConfigurer, authorizationServer -> {
                    authorizationServer.oidc(Customizer.withDefaults());
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
                .formLogin(Customizer.withDefaults())
                .rememberMe(remember -> {
                    remember.key("remember-me");
                })
//                .addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
        return httpSecurity.build();
    }

}
