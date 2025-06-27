package io.github.mikeiansky.oauth2.authorization.server.model.entity;

import lombok.Data;

import java.time.Instant;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
@Data
public class Authorization {

    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String attributes;

    private Principal principalHolder;

    // consent \ code \ state flow
    private String state;

    // accessToken Info
    private String accessTokenId;

    // refreshToken Info
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    private String refreshTokenMetadata;

    // oidcIdToken Info
    private String oidcIdTokenValue;
    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String oidcIdTokenClaims;


}
