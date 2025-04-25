package io.github.mikeiansky.oauth2.authorization.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mike ian
 * @date 2025/4/25
 * @desc
 **/
public class TestObjectWrapper {

    public static String testWriteValue() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "hello");
        map.put("exp", Instant.now());
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        String result = objectMapper.writeValueAsString(map);
        System.out.println(result);
        return result;
    }

    public static void testReadValue(String json) throws JsonProcessingException {
//        String json = """
//            {
//                "exp": "2025-04-25T01:55:17.168451Z",
//                "iat": "2025-04-25T01:50:17.168451Z",
//                "jti": "6b034114-0974-46d6-9271-b2ad569a80fe"
//            }
//        """;
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
//        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModules(securityModules);
//        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        Map<String,Object> data = objectMapper.readValue(json, new TypeReference<>() {
        });
        System.out.println(data);
    }

    public static void main(String[] args) throws JsonProcessingException {
        String temp = testWriteValue();
        testReadValue(temp);
    }


}
