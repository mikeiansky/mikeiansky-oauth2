package io.github.mikeiansky.oauth2.authorization.server.config;

import io.github.mikeiansky.oauth2.authorization.server.service.RedisSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.CookieRequestCache;

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
                .with(authorizationServerConfigurer, authorizationServer->
                        authorizationServer.oidc(Customizer.withDefaults())
                )
                .requestCache(requestCache -> {
                    requestCache.requestCache(new CookieRequestCache());
                })
                .securityContext(securityContext-> {
                    securityContext.securityContextRepository(new RedisSecurityContextRepository(redisTemplate));
                })
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .rememberMe(remember->{
                    remember.key("remember-me");
                })
        ;
        return httpSecurity.build();
    }

}
