package io.github.mikeiansky.oauth2.authorization.server.config;

import io.github.mikeiansky.oauth2.authorization.server.context.OpaqueTokenIntrospectorImpl;
import io.github.mikeiansky.oauth2.authorization.server.context.UuidOAuth2TokenGenerator;
import io.github.mikeiansky.oauth2.authorization.server.filter.LoginFilter;
import io.github.mikeiansky.oauth2.authorization.server.filter.LoginPageFilter;
import io.github.mikeiansky.oauth2.authorization.server.filter.SendCodeFilter;
import io.github.mikeiansky.oauth2.authorization.server.service.RedisSecurityContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.TemplateEngine;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   RedisTemplate<String, String> redisTemplate,
                                                   TemplateEngine templateEngine) throws Exception {


        httpSecurity.setSharedObject(OAuth2TokenGenerator.class, new UuidOAuth2TokenGenerator());

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        RedisSecurityContextRepository redisSecurityContextRepository = new RedisSecurityContextRepository(redisTemplate);
        CookieRequestCache requestCache = new CookieRequestCache();
        SendCodeFilter sendCodeFilter = new SendCodeFilter(redisTemplate);
        LoginPageFilter loginPageFilter = new LoginPageFilter(templateEngine);
        LoginFilter loginFilter = new LoginFilter(redisTemplate, redisSecurityContextRepository, requestCache);

        OpaqueTokenIntrospectorImpl opaqueTokenIntrospector = new OpaqueTokenIntrospectorImpl(redisTemplate);

        httpSecurity
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/oauth2/**"),   // 授权端点
                        new AntPathRequestMatcher("/passport/**"),   // passport 端点
                        new AntPathRequestMatcher("/login"),       // 登录页面
                        new AntPathRequestMatcher("/logout")       // 登出端点
                ))
                .with(authorizationServerConfigurer, authorizationServer -> {
                    authorizationServer.oidc(Customizer.withDefaults());
                })
                .requestCache(requestCacheConfigurer -> {
                    requestCacheConfigurer.requestCache(requestCache);
                })
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(redisSecurityContextRepository);
                })
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
                .authenticationProvider(new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector))
//                .formLogin(Customizer.withDefaults())
                .rememberMe(remember -> {
                    remember.key("remember-me");
                })
                .addFilterAfter(sendCodeFilter, LogoutFilter.class)
                .addFilterAfter(loginPageFilter, SendCodeFilter.class)
                .addFilterAfter(loginFilter, LoginPageFilter.class)
                .exceptionHandling(exception -> {
                    log.info("exceptionHandling ::: exp {}", exception);
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        log.info("authenticationEntryPoint ::::::  + url : {}", request.getRequestURI());
                        authException.printStackTrace();
                        // 如果是移动端这返回错误
                        response.sendRedirect(request.getContextPath() + "/passport/login");
                    });
                })
        ;
        return httpSecurity.build();
    }

}
