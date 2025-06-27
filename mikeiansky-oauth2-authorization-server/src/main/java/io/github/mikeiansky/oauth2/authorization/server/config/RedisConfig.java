package io.github.mikeiansky.oauth2.authorization.server.config;

/**
 * @author mike ian
 * @date 2025/4/24
 * @desc
 **/
public final class RedisConfig {

    private static final String DEFAULT_PREFIX = "oauth2:server";

    private static String prefix = DEFAULT_PREFIX;

    public static void setPrefix(String prefix) {
        RedisConfig.prefix = prefix;
    }

    public static String getLoginTokenKey(String token) {
        return String.format("%s:login:token:%s", prefix, token);
    }

    public static String getLoginUserKey(String user, String channel) {
        return String.format("%s:login:user:%s:%s", prefix, channel, user);
    }

    public static String getAuthorizationIdKey(String id) {
        return String.format("%s:authorization:id:%s", prefix, id);
    }

    public static String getAuthorizationRequestKey(String id) {
        return String.format("%s:authorization:request:%s", prefix, id);
    }

    public static String getAuthorizationPrincipalKey(String id) {
        return String.format("%s:authorization:principal:%s", prefix, id);
    }

    public static String getAuthorizationCodeKey(String code) {
        return String.format("%s:authorization:code:%s", prefix, code);
    }

    public static String getAuthorizationStateKey(String state) {
        return String.format("%s:authorization:state:%s", prefix, state);
    }

    public static String getAccessTokenKey(String accessToken) {
        return String.format("%s:token:access_token:%s", prefix, accessToken);
    }

    public static String getRefreshTokenKey(String refreshToken) {
        return String.format("%s:token:refresh_token:%s", prefix, refreshToken);
    }

    public static String getOidcIdTokenKey(String idToken) {
        return String.format("%s:token:oidc_id_token:%s", prefix, idToken);
    }

}
