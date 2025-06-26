package io.github.mikeiansky.oauth2.authorization.server;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationEntity;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
public class TestSerialize {

    public static void main(String[] args) throws JsonProcessingException {

        RegisteredClient client = RegisteredClient.withId("001")
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("test")
                .build();

        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        String result = objectMapper.writeValueAsString(client);
        System.out.println(result);
        System.out.println("\n ====== json ");
        System.out.println(JSON.toJSONString(client));

        System.out.println("\n ===== deserialize");
        RegisteredClient data = objectMapper.readValue(result, new TypeReference<>() {
        });
        System.out.println(data);

    }

}
