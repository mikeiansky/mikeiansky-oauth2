package io.github.mikeiansky.oauth2.authorization.server.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
@Slf4j
@Service
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private final RedisTemplate<String, String> redisTemplate;

    public OAuth2AuthorizationServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        log.info("save authorization: {} ", JSON.toJSONString(authorization));

        String authorizationId = authorization.getId();

        Authorization entity = new Authorization();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());

        // authorization code request consent state
        String state = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (state != null) {
            String stateId = MD5.create().digestHex(state, StandardCharsets.UTF_8);
            String stateKey = RedisConfig.getAuthorizationStateKey(stateId);
            redisTemplate.opsForValue().set(stateKey, authorizationId, 5, TimeUnit.MINUTES);
            entity.setState(state);
        } else {
            // 删除对应的 state TODO
        }

        // authorization code token
        OAuth2Authorization.Token<OAuth2AuthorizationCode> codeToken = authorization.getToken(OAuth2AuthorizationCode.class);
        if (codeToken != null) {
            String codeTokenId = MD5.create().digestHex(codeToken.getToken().getTokenValue(), StandardCharsets.UTF_8);
            AuthorizationCode code = new AuthorizationCode();
            code.setAuthorizationId(authorizationId);
            code.setValue(codeToken.getToken().getTokenValue());
            code.setIssuedAt(codeToken.getToken().getIssuedAt());
            code.setExpiresAt(codeToken.getToken().getExpiresAt());
            code.setMetadata(JSON.toJSONString(codeToken.getMetadata()));

            String codeTokenKey = RedisConfig.getAuthorizationCodeKey(codeTokenId);
            Instant now = Instant.now();
            long diffSecond = codeToken.getToken().getExpiresAt().getEpochSecond() - now.getEpochSecond();
            if (diffSecond > 0) {
                redisTemplate.opsForValue().set(codeTokenKey, JSON.toJSONString(code), diffSecond, TimeUnit.SECONDS);
            }
        }

        // authorization code request
        OAuth2AuthorizationRequest authorizationRequest = authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
        if (authorizationRequest != null) {
            String authorizeRequestKey = RedisConfig.getAuthorizationRequestKey(authorizationId);
            AuthorizationRequest authorizationRequestEntity = new AuthorizationRequest();
            authorizationRequestEntity.setAuthorizationRequestUri(authorizationRequest.getAuthorizationRequestUri());
            authorizationRequestEntity.setAuthorizationUri(authorizationRequest.getAuthorizationUri());
            authorizationRequestEntity.setRedirectUri(authorizationRequest.getRedirectUri());
            authorizationRequestEntity.setClientId(authorizationRequest.getClientId());
            authorizationRequestEntity.setGrantType(authorizationRequest.getGrantType().getValue());
            authorizationRequestEntity.setResponseType(authorizationRequest.getResponseType().getValue());
            authorizationRequestEntity.setState(authorizationRequest.getState());
            authorizationRequestEntity.setScope(String.join(",", authorizationRequest.getScopes()));
            authorizationRequestEntity.setAttributes(authorizationRequest.getAttributes());
            authorizationRequestEntity.setAdditionalParameters(authorizationRequest.getAdditionalParameters());
            if (authorizationRequest.getScopes() != null) {
                entity.setAuthorizedScopes(String.join(",", authorizationRequest.getScopes()));
            }
            redisTemplate.opsForValue().set(authorizeRequestKey, JSON.toJSONString(authorizationRequestEntity), 5, TimeUnit.MINUTES);

            String authorizePrincipalKey = RedisConfig.getAuthorizationPrincipalKey(authorizationId);
            Authentication principal = authorization.getAttribute(java.security.Principal.class.getName());
            Principal principalHolder = new Principal();
            principalHolder.setAuthorities(principal.getAuthorities().stream().map(String::valueOf).collect(Collectors.toSet()));
            principalHolder.setName(principal.getName());
            redisTemplate.opsForValue().set(authorizePrincipalKey, JSON.toJSONString(principalHolder), 5, TimeUnit.MINUTES);
        }

        // access token
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            AccessToken token = new AccessToken();
            token.setAuthorizationId(authorizationId);
            token.setValue(accessToken.getToken().getTokenValue());
            token.setIssuedAt(accessToken.getToken().getIssuedAt());
            token.setExpiresAt(accessToken.getToken().getExpiresAt());
            token.setTokenType(accessToken.getToken().getTokenType());
            token.setScopes(accessToken.getToken().getScopes());
            token.setMetadata(JSON.toJSONString(accessToken.getMetadata()));
            token.setGrantType(authorization.getAuthorizationGrantType());

            String accessTokenKey = RedisConfig.getAccessTokenKey(accessToken.getToken().getTokenValue());
            redisTemplate.opsForValue().set(accessTokenKey, JSON.toJSONString(token));
        }

        // refresh token
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
//            String refreshTokenId = IdUtil.fastSimpleUUID();
            String refreshTokenKey = RedisConfig.getRefreshTokenKey(refreshToken.getToken().getTokenValue());
            redisTemplate.opsForValue().set(refreshTokenKey, authorizationId);

            entity.setRefreshTokenValue(refreshToken.getToken().getTokenValue());
            entity.setRefreshTokenIssuedAt(refreshToken.getToken().getIssuedAt());
            entity.setRefreshTokenExpiresAt(refreshToken.getToken().getExpiresAt());
            entity.setRefreshTokenMetadata(JSON.toJSONString(refreshToken.getMetadata()));
        }

        // oidc id token
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            String oidcIdTokenId = IdUtil.fastSimpleUUID();
            String oidcIdTokenKey = RedisConfig.getOidcIdTokenKey(oidcIdTokenId);
            redisTemplate.opsForValue().set(oidcIdTokenKey, authorizationId);

            entity.setOidcIdTokenValue(oidcIdToken.getToken().getTokenValue());
            entity.setOidcIdTokenIssuedAt(oidcIdToken.getToken().getIssuedAt());
            entity.setOidcIdTokenExpiresAt(oidcIdToken.getToken().getExpiresAt());
            entity.setOidcIdTokenClaims(JSON.toJSONString(oidcIdToken.getClaims()));
            entity.setOidcIdTokenMetadata(JSON.toJSONString(oidcIdToken.getMetadata()));
        }

        // authorization info
        String authorizeIdKey = RedisConfig.getAuthorizationIdKey(authorizationId);
        redisTemplate.opsForValue().set(authorizeIdKey, JSON.toJSONString(entity), Duration.ofDays(7));
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
        log.info("findByToken token: {}, tokenType: {}", token, tokenType != null ? tokenType.getValue() : null);
        String authorizeId = null;
        AuthorizationCode authorizationCode = null;
        if (tokenType == null) {
            tokenType = OAuth2TokenType.ACCESS_TOKEN;
        }
        if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            String stateId = MD5.create().digestHex(token, StandardCharsets.UTF_8);
            authorizeId = redisTemplate.opsForValue().get(RedisConfig.getAuthorizationStateKey(stateId));
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            String codeTokenId = MD5.create().digestHex(token, StandardCharsets.UTF_8);
            String authorizationCodeContent = redisTemplate.opsForValue().get(RedisConfig.getAuthorizationCodeKey(codeTokenId));
            if (authorizationCodeContent == null) {
                log.error("find authorizationCodeContent is null");
                return null;
            }
            authorizationCode = JSON.parseObject(authorizationCodeContent, AuthorizationCode.class);
            if (authorizationCode == null) {
                log.error("find authorizationCode is null");
                return null;
            }
            authorizeId = authorizationCode.getAuthorizationId();
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            AccessToken accessToken = JSON.parseObject(redisTemplate.opsForValue().get(RedisConfig.getAccessTokenKey(token)), AccessToken.class);
            if (accessToken == null) {
                log.error("find access token is null");
                return null;
            }
            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(RegisteredClient.withId(accessToken.getClientId()).build())
                    .id(accessToken.getAuthorizationId())
                    .principalName(accessToken.getPrincipalHolder().getName())
                    .authorizationGrantType(accessToken.getGrantType())
                    .authorizedScopes(accessToken.getScopes());
            OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    accessToken.getValue(),
                    accessToken.getIssuedAt(),
                    accessToken.getExpiresAt(),
                    accessToken.getScopes());
            authorizationBuilder.token(oAuth2AccessToken, metadata -> {
                if (accessToken.getMetadata() != null) {
                    metadata.putAll(JSON.parseObject(accessToken.getMetadata(), Map.class));
                }
            });
            return authorizationBuilder.build();
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            authorizeId = redisTemplate.opsForValue().get(RedisConfig.getOidcIdTokenKey(token));
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            authorizeId = redisTemplate.opsForValue().get(RedisConfig.getRefreshTokenKey(token));
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            // TODO
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            // TODO
        }

        if (authorizeId == null) {
            log.error("find authorize id is null");
            return null;
        }

        String authorizationCache = redisTemplate.opsForValue().get(RedisConfig.getAuthorizationIdKey(authorizeId));
        if (authorizationCache == null) {
            log.error("find authorization cache is null");
            return null;
        }
        Authorization authorizationEntity = JSON.parseObject(authorizationCache, Authorization.class);

        RegisteredClient client = RegisteredClient.withId(authorizationEntity.getRegisteredClientId())
                .clientId(authorizationEntity.getRegisteredClientId())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("")
                .build();

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(client)
                .id(authorizationEntity.getId())
                .principalName(authorizationEntity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorizationEntity.getAuthorizationGrantType()))
                .authorizedScopes(Set.of(authorizationEntity.getAuthorizedScopes().split(",")));

        // authorization request
        String authorizationRequestCache = redisTemplate.opsForValue().get(RedisConfig.getAuthorizationRequestKey(authorizeId));
        OAuth2AuthorizationRequest authorizationRequest = null;
        if (authorizationRequestCache != null) {
            AuthorizationRequest authorizationRequestEntity = JSON.parseObject(authorizationRequestCache.toString(), AuthorizationRequest.class);
            authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                    .clientId(authorizationRequestEntity.getClientId())
                    .state(authorizationRequestEntity.getState())
                    .scope(authorizationRequestEntity.getScope() == null ? new String[]{} :
                            authorizationRequestEntity.getScope().split(","))
                    .additionalParameters(authorizationRequestEntity.getAdditionalParameters())
                    .attributes(authorizationRequestEntity.getAttributes())
                    .authorizationUri(authorizationRequestEntity.getAuthorizationUri())
                    .authorizationRequestUri(authorizationRequestEntity.getAuthorizationRequestUri())
                    .redirectUri(authorizationRequestEntity.getRedirectUri())
                    .build();
        }
        if (authorizationRequest != null) {
            authorizationBuilder.attribute(OAuth2AuthorizationRequest.class.getName(), authorizationRequest);
        }

        // principal
        String authorizePrincipalKey = RedisConfig.getAuthorizationPrincipalKey(authorizeId);
        Object principalCache = redisTemplate.opsForValue().get(authorizePrincipalKey);
        if (principalCache != null) {
            Principal principalHolder = JSON.parseObject(principalCache.toString(), Principal.class);
            UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(
                    principalHolder.getName(),
                    null,
                    principalHolder.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
            );
            authorizationBuilder.attribute(java.security.Principal.class.getName(), authenticationToken);
        }

        // state
        if (authorizationEntity.getState() != null) {
            authorizationBuilder.attribute(OAuth2ParameterNames.STATE, authorizationEntity.getState());
        }

        // code token
        if (authorizationCode != null) {
            OAuth2AuthorizationCode codeToken = new OAuth2AuthorizationCode(
                    authorizationCode.getValue(),
                    authorizationCode.getIssuedAt(),
                    authorizationCode.getExpiresAt()
            );
            AuthorizationCode finalAuthorizationCode = authorizationCode;
            authorizationBuilder.token(codeToken, metadata -> {
                if (finalAuthorizationCode.getMetadata() != null) {
                    metadata.putAll(JSON.parseObject(finalAuthorizationCode.getMetadata(), Map.class));
                }
            });
        }

        OAuth2Authorization result = authorizationBuilder
                .build();
        log.info("find token is : {}", JSON.toJSONString(result));
        return result;
    }

}
