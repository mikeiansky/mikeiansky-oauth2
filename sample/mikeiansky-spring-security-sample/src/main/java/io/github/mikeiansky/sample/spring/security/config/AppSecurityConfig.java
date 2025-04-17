package io.github.mikeiansky.sample.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author mike ian
 * @date 2025/4/17
 * @desc
 **/
@Configuration
public class AppSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests ->authorizeRequests.anyRequest().authenticated())
        ;
        return http.build();
    }

}
