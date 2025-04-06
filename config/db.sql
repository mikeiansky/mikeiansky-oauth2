-- 创建用户表
CREATE TABLE `user_account` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mobile` varchar(16) DEFAULT NULL COMMENT '手机号码',
    `area_code` varchar(8) DEFAULT NULL COMMENT '手机区号',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
    `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `portrait` varchar(1024) DEFAULT NULL COMMENT '头像',
    `status` tinyint(4) DEFAULT NULL COMMENT '用户状态',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mobile_ac` (`mobile`,`area_code`) USING BTREE COMMENT '手机号码唯一索引',
    UNIQUE KEY `uk_email` (`email`) USING BTREE COMMENT '邮箱唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账户表';

-- 创建用户权限表
CREATE TABLE `user_authority` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `authority` varchar(50) NOT NULL COMMENT '权限',
    PRIMARY KEY (`id`),
    KEY `idx_uid` (`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户权限表';

-- OAuth2 客户端信息表
CREATE TABLE `oauth2_client` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增ID',
    `client_id` varchar(255) NOT NULL COMMENT '客户端ID，唯一标识OAuth2客户端',
    `client_id_issued_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '客户端ID发放时间',
    `client_secret` varchar(255) DEFAULT NULL COMMENT '客户端密钥，存储加密后的密钥',
    `client_secret_expires_at` datetime DEFAULT NULL COMMENT '客户端密钥过期时间',
    `client_name` varchar(255) NOT NULL COMMENT '客户端名称',
    `client_authentication_methods` varchar(1000) NOT NULL COMMENT '客户端认证方式（如client_secret_basic等）',
    `authorization_grant_types` varchar(1000) NOT NULL COMMENT '授权模式（如authorization_code, client_credentials等）',
    `redirect_uris` varchar(1000) DEFAULT NULL COMMENT '授权成功后的回调地址（可多个）',
    `post_logout_redirect_uris` varchar(1000) DEFAULT NULL COMMENT '注销后的回调地址',
    `scopes` varchar(1000) NOT NULL COMMENT '客户端可访问的权限范围',
    `client_settings` varchar(2000) NOT NULL COMMENT '客户端相关配置（JSON格式）',
    `token_settings` varchar(2000) NOT NULL COMMENT '令牌相关配置（JSON格式）',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OAuth2 客户端信息表';

-- 新增一条客户端数据
INSERT INTO `mikeiansky_oauth2`.`oauth2_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`, `update_time`, `create_time`) VALUES (1, 'login-client', '2025-04-02 14:07:49', '{noop}openid-connect', '2025-06-02 14:07:20', 'login-client', 'client_secret_basic', 'authorization_code,refresh_token', 'http://127.0.0.1:8001/login/oauth2/code/login-client,http://127.0.0.1:8001/authorized', NULL, 'openid,profile', '{\"settings.client.require-authorization-consent\":true,\"settings.client.require-proof-key\":false}', ' ', '2025-04-02 14:41:11', '2025-04-02 14:07:49');

-- 更新之前的客户端数据
UPDATE `mikeiansky_oauth2`.`oauth2_client` SET `client_id` = 'login-client', `client_id_issued_at` = '2025-04-02 14:07:49', `client_secret` = '{noop}openid-connect', `client_secret_expires_at` = '2025-06-02 14:07:20', `client_name` = 'login-client', `client_authentication_methods` = 'client_secret_basic', `authorization_grant_types` = 'authorization_code,refresh_token', `redirect_uris` = 'http://127.0.0.1:8001/login/oauth2/code/login-client,http://127.0.0.1:8001/authorized', `post_logout_redirect_uris` = NULL, `scopes` = 'openid,profile', `client_settings` = '{\"settings.client.require-authorization-consent\":true,\"settings.client.require-proof-key\":false}', `token_settings` = '{\r\n    \"settings.token.access-token-format\": \"reference\",\r\n    \"settings.token.authorization-code-time-to-live\": \"5\",\r\n    \"settings.token.access-token-time-to-live\": \"5\",\r\n    \"settings.token.refresh-token-time-to-live\": \"5\"\r\n}', `update_time` = '2025-04-06 04:10:42', `create_time` = '2025-04-02 14:07:49' WHERE `id` = 1;
