package io.github.mikeiansky.oauth2.authorization.server.obj;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.AccessToken;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.Principal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
public class TestOAuth2AccessTokenHolder {

    public static void main(String[] args) {

        Principal principalHolder = new Principal();
        principalHolder.setName("test");

        AccessToken accessTokenHolder = new AccessToken();
        accessTokenHolder.setPrincipalHolder(principalHolder);
        accessTokenHolder.setTokenType(OAuth2AccessToken.TokenType.BEARER);

        String data = JSON.toJSONString(accessTokenHolder);
        System.out.println(data);
        accessTokenHolder = JSON.parseObject(data, AccessToken.class);
        System.out.println(accessTokenHolder);
        System.out.println(accessTokenHolder.getTokenType().getValue());

    }

}
