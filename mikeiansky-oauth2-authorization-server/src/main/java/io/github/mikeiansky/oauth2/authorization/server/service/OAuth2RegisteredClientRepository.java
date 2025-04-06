package io.github.mikeiansky.oauth2.authorization.server.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.github.mikeiansky.oauth2.model.entity.Oauth2Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mike ian
 * @date 2025/4/2
 * @desc OAuth2 客户端存储服务
 **/
@Slf4j
@Service
public class OAuth2RegisteredClientRepository implements RegisteredClientRepository {

    @Override
    public void save(RegisteredClient registeredClient) {
        log.info("save registered client {}", registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        log.info("findById id : {}", id);
        return getRegisteredClient(id);
    }

    private static RegisteredClient getRegisteredClient(String id) {
        Oauth2Client oauth2Client = Db.lambdaQuery(Oauth2Client.class)
                .eq(Oauth2Client::getClientId, id)
                .one();

        if (oauth2Client == null) {
            return null;
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(oauth2Client.getClientIdIssuedAt(), formatter);
        Instant issuedAt = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return RegisteredClient.withId(oauth2Client.getClientId())
                .clientId(oauth2Client.getClientId())
                .clientIdIssuedAt(issuedAt)
                .clientSecret(oauth2Client.getClientSecret())
                .clientName(oauth2Client.getClientName())
                .clientAuthenticationMethods(methods -> methods.addAll(
                        Stream.of(Optional.ofNullable(oauth2Client.getClientAuthenticationMethods()).orElse("").split(","))
                                .map(ClientAuthenticationMethod::new)
                                .collect(Collectors.toSet())
                ))
                .authorizationGrantTypes(types -> types.addAll(
                        Stream.of(Optional.ofNullable(oauth2Client.getAuthorizationGrantTypes()).orElse("").split(","))
                                .map(AuthorizationGrantType::new)
                                .collect(Collectors.toSet())
                ))
                .redirectUris(uris -> uris.addAll(
                        Arrays.asList(Optional.ofNullable(oauth2Client.getRedirectUris()).orElse("").split(","))
                ))
                .postLogoutRedirectUris(uris -> uris.addAll(
                        Arrays.asList(Optional.ofNullable(oauth2Client.getPostLogoutRedirectUris()).orElse("").split(","))
                ))
                .scopes(scopes -> scopes.addAll(
                        Arrays.asList(Optional.ofNullable(oauth2Client.getScopes()).orElse("").split(","))
                ))
                .clientSettings(
                        StringUtils.hasText(oauth2Client.getClientSettings()) ?
                                ClientSettings.withSettings(JSONObject.parseObject(oauth2Client.getClientSettings()))
                                        .requireAuthorizationConsent(true)
                                        .build() :
                                null
                )
                .tokenSettings(
                        StringUtils.hasText(oauth2Client.getTokenSettings()) ?
                                TokenSettings.withSettings(JSONObject.parseObject(oauth2Client.getTokenSettings())).build() :
                                null)
                .build();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.info("findByClientId clientId : {}", clientId);
        return getRegisteredClient(clientId);
    }

}
