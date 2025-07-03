package io.github.mikeiansky.oauth2.authorization.server.context;

import com.alibaba.fastjson2.JSON;
import io.github.mikeiansky.oauth2.authorization.server.config.RedisConfig;
import io.github.mikeiansky.oauth2.authorization.server.model.entity.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author mike ian
 * @date 2025/7/3
 * @desc
 **/
@Slf4j
public class OpaqueTokenIntrospectorImpl implements OpaqueTokenIntrospector {

    private final RedisTemplate<String, String> redisTemplate;

    public OpaqueTokenIntrospectorImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {

        String accessTokenKey = RedisConfig.getAccessTokenKey(token);
        String accessTokenValue = redisTemplate.opsForValue().get(accessTokenKey);
        log.info("access token key: {}, value : {}", accessTokenKey, accessTokenValue);
        AccessToken accessToken = JSON.parseObject(accessTokenValue, AccessToken.class);

        if (accessToken == null) {
            return new DefaultOAuth2AuthenticatedPrincipal(new HashMap<>(), Collections.emptyList());
        }
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(OAuth2TokenIntrospectionClaimNames.IAT, accessToken.getIssuedAt());
        metadata.put(OAuth2TokenIntrospectionClaimNames.EXP, accessToken.getExpiresAt());
        List<GrantedAuthority> grantedAuthorityList = Optional.ofNullable(accessToken.getScopes())
                .orElse(Collections.emptySet())
                .stream()
                .map(s -> new SimpleGrantedAuthority(s))
                .collect(Collectors.toList());
        DefaultOAuth2AuthenticatedPrincipal result = new DefaultOAuth2AuthenticatedPrincipal(
                Collections.unmodifiableMap(metadata), grantedAuthorityList);
        return result;
    }

}
