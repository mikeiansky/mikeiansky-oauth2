package io.github.mikeiansky.oauth2.authorization.server.model.entity;

import lombok.Data;

import java.time.Instant;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
@Data
public class AuthorizationCode {

    private String authorizationId;

    private String value;
    private Instant issuedAt;
    private Instant expiresAt;
    private String metadata;

}
