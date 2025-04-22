package io.github.mikeiansky.oauth2.authorization.server.service;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationEntity;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationRequestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
@Slf4j
@Service
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private final RedisTemplate<String, Object> redisTemplate;

    public OAuth2AuthorizationServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private OAuth2AuthorizationRequestEntity toAuthorizationRequestEntity(OAuth2Authorization authorization) {
        OAuth2AuthorizationRequest authorizationRequest = authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
        if (authorizationRequest == null) {
            return null;
        }
        OAuth2AuthorizationRequestEntity requestEntity = new OAuth2AuthorizationRequestEntity();
        requestEntity.setAuthorizationRequestUri(authorizationRequest.getAuthorizationRequestUri());
        requestEntity.setAuthorizationUri(authorizationRequest.getAuthorizationUri());
        requestEntity.setClientId(authorizationRequest.getClientId());
        requestEntity.setGrantType(authorizationRequest.getGrantType().getValue());
        requestEntity.setRedirectUri(authorizationRequest.getRedirectUri());
        requestEntity.setResponseType(authorizationRequest.getResponseType().getValue());
        requestEntity.setScope(String.join(",", authorizationRequest.getScopes()));
        requestEntity.setState(authorizationRequest.getState());
        requestEntity.setAdditionalParameters(authorizationRequest.getAdditionalParameters());
        requestEntity.setAttributes(authorizationRequest.getAttributes());

        return requestEntity;
    }

    private OAuth2AuthorizationRequest toAuthorizationRequest(OAuth2AuthorizationRequestEntity requestEntity) {
        if (requestEntity == null) {
            return null;
        }
        OAuth2AuthorizationRequest request = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationRequestUri(requestEntity.getAuthorizationRequestUri())
                .authorizationUri(requestEntity.getAuthorizationUri())
                .clientId(requestEntity.getClientId())
                .redirectUri(requestEntity.getRedirectUri())
                .state(requestEntity.getState())
                .scope(requestEntity.getScope().split(","))
                .attributes(requestEntity.getAttributes())
                .additionalParameters(requestEntity.getAdditionalParameters())
                .build();

        return request;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        log.info("save authorization: {} ", JSON.toJSONString(authorization));
        OAuth2AuthorizationEntity entity = new OAuth2AuthorizationEntity();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizedScopes(String.join(",", authorization.getAuthorizedScopes()));
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        String authorizationKey = "state:" + entity.getState();
        redisTemplate.opsForValue().set(authorizationKey, JSON.toJSONString(entity));
        String requestKey = "request:" + entity.getId();
        OAuth2AuthorizationRequestEntity request = toAuthorizationRequestEntity(authorization);
        redisTemplate.opsForValue().set(requestKey, JSON.toJSONString(request));
    }

    @Override
    public void remove(OAuth2Authorization authorization) {

    }

    @Override
    public OAuth2Authorization findById(String id) {
        return null;
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        log.info("findByToken token: {}, tokenType: {}", token, tokenType.getValue());
        String authorizationKey = "state:" + token;
        Object stateCache = redisTemplate.opsForValue().get(authorizationKey);
        if (stateCache == null) {
            return null;
        }
        OAuth2AuthorizationEntity authorizationEntity = JSON.parseObject(stateCache.toString(), OAuth2AuthorizationEntity.class);
        String requestKey = "request:" + authorizationEntity.getId();
        Object requestCache = redisTemplate.opsForValue().get(requestKey);
        if (requestCache == null) {
            return null;
        }
        OAuth2AuthorizationRequestEntity requestEntity = JSON.parseObject(requestCache.toString(), OAuth2AuthorizationRequestEntity.class);
        OAuth2AuthorizationRequest request = toAuthorizationRequest(requestEntity);

        RegisteredClient client = RegisteredClient.withId(requestEntity.getClientId())
                .clientId(requestEntity.getClientId())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(requestEntity.getRedirectUri())
                .build();

        OAuth2Authorization result = OAuth2Authorization.withRegisteredClient(client)
                .id(authorizationEntity.getId())
                .principalName(authorizationEntity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorizationEntity.getAuthorizationGrantType()))
                .authorizedScopes(Set.of(authorizationEntity.getAuthorizedScopes().split(",")))
                .attribute(OAuth2AuthorizationRequest.class.getName(), request)
                .build();

        return result;
    }

}
