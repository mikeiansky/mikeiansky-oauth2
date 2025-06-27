package io.github.mikeiansky.oauth2.authorization.server.model.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
@Data
public class AuthorizationRequest {

    private String authorizationRequestUri;
    private String authorizationUri;
    private String clientId;
    private String grantType;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String state;
    private Map<String, Object> attributes;
    private Map<String, Object> additionalParameters;
    private Principal principalHolder;

}
