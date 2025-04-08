package io.github.mikeiansky.oauth2.client.config;

import org.springframework.context.annotation.Configuration;


/**
 * @author mike ian
 * @date 2025/4/7
 * @desc 应用安全认证配置
 **/
//@EnableWebSecurity
@Configuration
public class AppSecurityContextConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorization -> authorization
//                                .authorizationRequestResolver(
//                                        authorizationRequestResolver(clientRegistrationRepository)
//                                )
//                        )
//                );
//        return http.build();
//    }
//
//    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
//            ClientRegistrationRepository clientRegistrationRepository) {
//
//        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
//                new DefaultOAuth2AuthorizationRequestResolver(
//                        clientRegistrationRepository, "/oauth2/authorization");
//        authorizationRequestResolver.setAuthorizationRequestCustomizer(
//                authorizationRequestCustomizer());
//
//        return authorizationRequestResolver;
//    }
//
//    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
//        return customizer -> customizer
//                .additionalParameters(params -> params.put("prompt", "consent"));
//    }

}
