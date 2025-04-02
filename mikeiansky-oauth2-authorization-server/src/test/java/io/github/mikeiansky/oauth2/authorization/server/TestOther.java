package io.github.mikeiansky.oauth2.authorization.server;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * @author mike ian
 * @date 2025/4/2
 * @desc
 **/
public class TestOther {

    public static void main(String[] args) {

//        String grantType = "openid,profile";
//        Stream.of(grantType.split(","))
//                .forEach(System.out::println);

        System.out.println(Instant.now());
        System.out.println(DateUtil.now());

        String now = DateUtil.now();
// 定义格式（确保匹配你的字符串格式）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(now, formatter);

        // 转换为 Instant，使用 UTC 时区
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        System.out.println(instant);

        System.out.println(ZoneId.systemDefault());

    }

}
