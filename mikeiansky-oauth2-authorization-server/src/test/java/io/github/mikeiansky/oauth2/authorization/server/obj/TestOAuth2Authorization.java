package io.github.mikeiansky.oauth2.authorization.server.obj;

import com.alibaba.fastjson2.JSON;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author mike ian
 * @date 2025/6/26
 * @desc
 **/
public class TestOAuth2Authorization {

    public static void main(String[] args) {

//        OAuth2Authorization authorization = new OAuth2Authorization();

        OAuth2Authorization authorization2 = OAuth2Authorization.from(null)
                .invalidate(null)
                .build();


        RegisteredClient client = RegisteredClient.withId("test0-01")
                .clientId("client0")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("")
                .build();

        System.out.println(JSON.toJSONString(client));

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "kkk",
                Instant.now(),
                Instant.now().plus(7, ChronoUnit.DAYS),
                Set.of("openid", "profile")
        );

        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(client)
                .principalName("pn-001")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .token(accessToken, new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> som) {

                    }
                })
                .attribute("k1", "v1")
                .attribute("k2", "v2")
                .build();


        authorization.getToken(OAuth2AccessToken.class).getClaims();

        System.out.println(JSON.toJSONString(authorization));


    }

}
