package io.github.mikeiansky.oauth2.authorization.server.context;

import cn.hutool.core.util.IdUtil;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author mike ian
 * @date 2025/7/3
 * @desc
 **/
public class UuidOAuth2TokenGenerator implements OAuth2TokenGenerator {

    @Override
    public OAuth2Token generate(OAuth2TokenContext context) {
        if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
            return new OAuth2RefreshToken(
                    IdUtil.fastSimpleUUID(),
                    Instant.now(),
                    Instant.now().plus(7, ChronoUnit.DAYS));
        }
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                IdUtil.fastSimpleUUID(),
                Instant.now(),
                Instant.now().plus(7, ChronoUnit.DAYS));
    }

}
