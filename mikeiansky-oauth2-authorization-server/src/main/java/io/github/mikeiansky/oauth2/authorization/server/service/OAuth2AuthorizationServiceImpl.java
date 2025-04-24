package io.github.mikeiansky.oauth2.authorization.server.service;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.AppRedisKeyConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationEntity;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.OAuth2AuthorizationRequestEntity;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.PrincipalHolder;
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

import java.security.Principal;
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

    private final RedisTemplate<String, Object> redisTemplate;

    public OAuth2AuthorizationServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        log.info("save authorization: {} ", JSON.toJSONString(authorization));

        String authorizeId = authorization.getId();

        OAuth2AuthorizationEntity entity = new OAuth2AuthorizationEntity();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());

        MD5 md5 = MD5.create();
        // authorization code request consent state
        String state = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (state != null) {
            String stateKey = AppRedisKeyConfig.getAuthorizeStateKey(state);
            redisTemplate.opsForValue().set(stateKey, authorizeId, 5, TimeUnit.MINUTES);
            entity.setState(state);
        }

        // authorization code token
        OAuth2Authorization.Token<OAuth2AuthorizationCode> codeToken = authorization.getToken(OAuth2AuthorizationCode.class);
        if (codeToken != null) {
            String codeTokenMd5 = md5.digestHex(codeToken.getToken().getTokenValue());
            String codeTokenKey = AppRedisKeyConfig.getAuthorizeCodeKey(codeTokenMd5);
            Instant now = Instant.now();
            long diffSecond = codeToken.getToken().getExpiresAt().getEpochSecond() - now.getEpochSecond();

            if (diffSecond > 0) {
                redisTemplate.opsForValue().set(codeTokenKey, authorizeId, diffSecond, TimeUnit.SECONDS);
            }

            entity.setAuthorizationCodeValue(codeToken.getToken().getTokenValue());
            entity.setAuthorizationCodeIssuedAt(codeToken.getToken().getIssuedAt());
            entity.setAuthorizationCodeExpiresAt(codeToken.getToken().getExpiresAt());
            entity.setAuthorizationCodeMetadata(JSON.toJSONString(codeToken.getMetadata()));
        }

        // authorization code request
        OAuth2AuthorizationRequest authorizationRequest = authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
        if (authorizationRequest != null) {
            String authorizeRequestKey = AppRedisKeyConfig.getAuthorizeRequestKey(authorizeId);
            OAuth2AuthorizationRequestEntity authorizationRequestEntity = new OAuth2AuthorizationRequestEntity();
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

            String authorizePrincipalKey = AppRedisKeyConfig.getAuthorizePrincipalKey(authorizeId);
            Authentication principal = authorization.getAttribute(Principal.class.getName());
            PrincipalHolder principalHolder = new PrincipalHolder();
            principalHolder.setAuthorities(principal.getAuthorities().stream().map(String::valueOf).collect(Collectors.toSet()));
            principalHolder.setName(principal.getName());
            redisTemplate.opsForValue().set(authorizePrincipalKey, JSON.toJSONString(principalHolder), 5, TimeUnit.MINUTES);
        }

        // access token
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            String accessTokenMd5 = md5.digestHex(accessToken.getToken().getTokenValue());
            String accessTokenKey = AppRedisKeyConfig.getAccessTokenKey(accessTokenMd5);
            redisTemplate.opsForValue().set(accessTokenKey, authorizeId);

            entity.setAccessTokenValue(accessToken.getToken().getTokenValue());
            entity.setAccessTokenIssuedAt(accessToken.getToken().getIssuedAt());
            entity.setAccessTokenExpiresAt(accessToken.getToken().getExpiresAt());
            entity.setAccessTokenType(accessToken.getToken().getTokenType().getValue());
            entity.setAccessTokenScopes(String.join(",", accessToken.getToken().getScopes()));
            entity.setAccessTokenMetadata(JSON.toJSONString(accessToken.getMetadata()));

        }

        // refresh token
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            String refreshTokenMd5 = md5.digestHex(refreshToken.getToken().getTokenValue());
            String refreshTokenKey = AppRedisKeyConfig.getRefreshTokenKey(refreshTokenMd5);
            redisTemplate.opsForValue().set(refreshTokenKey, authorizeId);

            entity.setRefreshTokenValue(refreshToken.getToken().getTokenValue());
            entity.setRefreshTokenIssuedAt(refreshToken.getToken().getIssuedAt());
            entity.setRefreshTokenExpiresAt(refreshToken.getToken().getExpiresAt());
            entity.setRefreshTokenMetadata(JSON.toJSONString(refreshToken.getMetadata()));
        }

        // oidc id token
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            String oidcIdTokenMd5 = md5.digestHex(oidcIdToken.getToken().getTokenValue());
            String oidcIdTokenKey = AppRedisKeyConfig.getOidcIdTokenKey(oidcIdTokenMd5);
            redisTemplate.opsForValue().set(oidcIdTokenKey, authorizeId);

            entity.setOidcIdTokenValue(oidcIdToken.getToken().getTokenValue());
            entity.setOidcIdTokenIssuedAt(oidcIdToken.getToken().getIssuedAt());
            entity.setOidcIdTokenExpiresAt(oidcIdToken.getToken().getExpiresAt());
            entity.setOidcIdTokenClaims(JSON.toJSONString(oidcIdToken.getClaims()));
            entity.setOidcIdTokenMetadata(JSON.toJSONString(oidcIdToken.getMetadata()));
        }

        // authorization info
        String authorizeIdKey = AppRedisKeyConfig.getAuthorizeIdKey(authorizeId);
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
        log.info("findByToken token: {}, tokenType: {}", token, tokenType.getValue());

        String authorizeId = null;
        Object authorizeIdCache = null;
        if (tokenType == null) {
            tokenType = OAuth2TokenType.ACCESS_TOKEN;
        }
        if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            authorizeIdCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getAuthorizeStateKey(token));
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            String codeMd5 = MD5.create().digestHex(token);
            authorizeIdCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getAuthorizeCodeKey(codeMd5));
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            String accessTokenMd5 = MD5.create().digestHex(token);
            authorizeIdCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getAccessTokenKey(accessTokenMd5));
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            String oidcIdTokenMd5 = MD5.create().digestHex(token);
            authorizeIdCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getOidcIdTokenKey(oidcIdTokenMd5));
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            String refreshTokenMd5 = MD5.create().digestHex(token);
            authorizeIdCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getRefreshTokenKey(refreshTokenMd5));
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
        }

        if (authorizeIdCache == null) {
            log.error("find authorize id is null");
            return null;
        }

        authorizeId = authorizeIdCache.toString();
        Object authorizationCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getAuthorizeIdKey(authorizeId));
        if (authorizationCache == null) {
            log.error("find authorization cache is null");
            return null;
        }
        OAuth2AuthorizationEntity authorizationEntity = JSON.parseObject(authorizationCache.toString(), OAuth2AuthorizationEntity.class);

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
        Object authorizationRequestCache = redisTemplate.opsForValue().get(AppRedisKeyConfig.getAuthorizeRequestKey(authorizeId));
        OAuth2AuthorizationRequest authorizationRequest = null;
        if (authorizationRequestCache != null) {
            OAuth2AuthorizationRequestEntity authorizationRequestEntity = JSON.parseObject(authorizationRequestCache.toString(), OAuth2AuthorizationRequestEntity.class);
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
        String authorizePrincipalKey = AppRedisKeyConfig.getAuthorizePrincipalKey(authorizeId);
        Object principalCache = redisTemplate.opsForValue().get(authorizePrincipalKey);
        if (principalCache != null) {
            PrincipalHolder principalHolder = JSON.parseObject(principalCache.toString(), PrincipalHolder.class);
            UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(
                    principalHolder.getName(),
                    null,
                    principalHolder.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
            );
            authorizationBuilder.attribute(Principal.class.getName(), authenticationToken);
        }

        // state
        if (authorizationEntity.getState() != null) {
            authorizationBuilder.attribute(OAuth2ParameterNames.STATE, authorizationEntity.getState());
        }

        // code token
        if (authorizationEntity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    authorizationEntity.getAuthorizationCodeValue(),
                    authorizationEntity.getAuthorizationCodeIssuedAt(),
                    authorizationEntity.getAuthorizationCodeExpiresAt()
            );
            authorizationBuilder.token(authorizationCode, metadata -> {
                if (authorizationEntity.getAuthorizationCodeMetadata() != null) {
                    metadata.putAll(JSON.parseObject(authorizationEntity.getAuthorizationCodeMetadata(), Map.class));
                }
            });
        }

        // access token
        if (authorizationEntity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    authorizationEntity.getAccessTokenValue(),
                    authorizationEntity.getAccessTokenIssuedAt(),
                    authorizationEntity.getAccessTokenExpiresAt(),
                    authorizationEntity.getAccessTokenScopes() == null ? Set.of() : Set.of(authorizationEntity.getAccessTokenScopes().split(",")));
            authorizationBuilder.token(accessToken, metadata -> {
                if (authorizationEntity.getAccessTokenMetadata() != null) {
                    metadata.putAll(JSON.parseObject(authorizationEntity.getAccessTokenMetadata(), Map.class));
                }
            });
        }

        // refresh token
        if (authorizationEntity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    authorizationEntity.getRefreshTokenValue(),
                    authorizationEntity.getRefreshTokenIssuedAt(),
                    authorizationEntity.getRefreshTokenExpiresAt());
            authorizationBuilder.token(refreshToken, metadata -> {
                if (authorizationEntity.getRefreshTokenMetadata() != null) {
                    metadata.putAll(JSON.parseObject(authorizationEntity.getRefreshTokenMetadata(), Map.class));
                }
            });
        }

        // oidc id token
        if (authorizationEntity.getOidcIdTokenValue() != null) {
            OidcIdToken oidcIdToken = new OidcIdToken(
                    authorizationEntity.getOidcIdTokenValue(),
                    authorizationEntity.getOidcIdTokenIssuedAt(),
                    authorizationEntity.getOidcIdTokenExpiresAt(),
                    JSON.parseObject(authorizationEntity.getOidcIdTokenClaims(), Map.class));
            authorizationBuilder.token(oidcIdToken, metadata -> {
                if (authorizationEntity.getOidcIdTokenMetadata() != null) {
                    metadata.putAll(JSON.parseObject(authorizationEntity.getOidcIdTokenMetadata(), Map.class));
                }
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, oidcIdToken.getClaims());
            });
        }

        OAuth2Authorization result = authorizationBuilder
                .build();
        log.info("find token is : {}", JSON.toJSONString(result));
        return result;
    }

}
