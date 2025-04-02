package io.github.mikeiansky.oauth2.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * OAuth2 客户端信息表
 * </p>
 *
 * @author Mikeiansky
 * @since 2025-04-02
 */
@Getter
@Setter
@ToString
@TableName("oauth2_client")
public class Oauth2Client implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户端ID，唯一标识OAuth2客户端
     */
    private String clientId;

    /**
     * 客户端ID发放时间
     */
    private LocalDateTime clientIdIssuedAt;

    /**
     * 客户端密钥，存储加密后的密钥
     */
    private String clientSecret;

    /**
     * 客户端密钥过期时间
     */
    private LocalDateTime clientSecretExpiresAt;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端认证方式（如client_secret_basic等）
     */
    private String clientAuthenticationMethods;

    /**
     * 授权模式（如authorization_code, client_credentials等）
     */
    private String authorizationGrantTypes;

    /**
     * 授权成功后的回调地址（可多个）
     */
    private String redirectUris;

    /**
     * 注销后的回调地址
     */
    private String postLogoutRedirectUris;

    /**
     * 客户端可访问的权限范围
     */
    private String scopes;

    /**
     * 客户端相关配置（JSON格式）
     */
    private String clientSettings;

    /**
     * 令牌相关配置（JSON格式）
     */
    private String tokenSettings;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
