package io.github.mikeiansky.oauth2.authorization.server;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.io.Serializable;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
public class TestSerialize {

    public static void main(String[] args) {

        RegisteredClient client = RegisteredClient.withId("001")
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("test")
                .build();


    }

}
