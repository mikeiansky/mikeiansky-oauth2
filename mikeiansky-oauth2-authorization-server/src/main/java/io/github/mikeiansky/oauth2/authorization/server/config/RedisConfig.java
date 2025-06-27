package io.github.mikeiansky.oauth2.authorization.server.config;

/**
 * @author mike ian
 * @date 2025/4/24
 * @desc
 **/
public final class RedisConfig {

    private static final String OAUTH2_SERVER_PREFIX = "oauth2:server";

    public static String getLoginTokenKey(String token) {
        return String.format("%s:login:token:%s", OAUTH2_SERVER_PREFIX, token);
    }

    public static String getLoginUserKey(String user, String channel) {
        return String.join("%s:login:user:%s:%s", OAUTH2_SERVER_PREFIX, channel, user);
    }

    public static String getAuthorizeIdKey(String id) {
        return OAUTH2_SERVER_PREFIX + ":authorization:id:" + id;
    }

    public static String getAuthorizeRequestKey(String id) {
        return OAUTH2_SERVER_PREFIX + ":authorization:request:" + id;
    }

    public static String getAuthorizePrincipalKey(String id) {
        return OAUTH2_SERVER_PREFIX + ":authorization:principal:" + id;
    }

    public static String getAuthorizeCodeKey(String code) {
        return OAUTH2_SERVER_PREFIX + ":authorization:code:" + code;
    }

    public static String getAuthorizeStateKey(String state) {
        return OAUTH2_SERVER_PREFIX + ":authorization:state:" + state;
    }

    public static String getAccessTokenKey(String accessToken) {
        return OAUTH2_SERVER_PREFIX + ":token:access_token:" + accessToken;
    }

    public static String getRefreshTokenKey(String refreshToken) {
        return OAUTH2_SERVER_PREFIX + ":token:refresh_token:" + refreshToken;
    }

    public static String getOidcIdTokenKey(String idToken) {
        return OAUTH2_SERVER_PREFIX + ":token:oidc_id_token:" + idToken;
    }


}
