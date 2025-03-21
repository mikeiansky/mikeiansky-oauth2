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