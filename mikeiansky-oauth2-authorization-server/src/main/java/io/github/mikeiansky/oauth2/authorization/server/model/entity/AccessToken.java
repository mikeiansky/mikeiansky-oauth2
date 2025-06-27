package io.github.mikeiansky.oauth2.authorization.server.model.entity;

import lombok.Data;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.Set;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
@Data
public class AccessToken {

    private OAuth2AccessToken.TokenType tokenType;

    private Set<String> scopes;

    private String value;

    private Instant issuedAt;

    private Instant expiresAt;

    private Principal principalHolder;

    private String clientId;

    private String metadata;

    private String id;

    private AuthorizationGrantType grantType;

    private String authorizationId;

}
