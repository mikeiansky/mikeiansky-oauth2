package io.github.mikeiansky.oauth2.authorization.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;


/**
 * @author mike ian
 * @date 2025/3/21
 * @desc 应用Security配置
 **/
@Configuration
public class AppSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        httpSecurity
                .with(authorizationServerConfigurer, authorizationServer-> authorizationServer.oidc(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .rememberMe(remember->{
                    remember.key("remember-me-02");
                })
        ;
        return httpSecurity.build();
    }

}
