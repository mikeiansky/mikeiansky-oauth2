package io.github.mikeiansky.oauth2.authorization.server.obj;

import com.alibaba.fastjson2.JSON;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 *
 * @author mike ian
 * @date 2025/6/26
 * @desc
 **/
public class TestOAuth2AuthorizationRequest {

    public static void main(String[] args) {
        OAuth2AuthorizationRequest request = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://github.com/mikeiansky/oauth2/authorize")
                .clientId("client-id")
                .state("state")
                .build();
        System.out.println(JSON.toJSONString(request));
    }

}
