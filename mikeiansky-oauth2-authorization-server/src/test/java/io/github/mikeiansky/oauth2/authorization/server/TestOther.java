package io.github.mikeiansky.oauth2.authorization.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author mike ian
 * @date 2025/4/2
 * @desc
 **/
public class TestOther {

    public static void main(String[] args) {
        String key = UUID.randomUUID().toString();
        System.out.println(key);
        String hash1 = DigestUtils.sha256Hex(key);
        System.out.println(hash1);
        String hash2 = DigestUtils.sha1Hex(key);
        System.out.println(hash2);
        String hash3 = DigestUtils.md2Hex(key);
        System.out.println(hash3);
        String hash4 = DigestUtils.md5Hex(key);
        System.out.println(hash4);

        // username, mobile, email，发送验证码

//        GSON.toJson("Invalid state parameter.");
    }

}
