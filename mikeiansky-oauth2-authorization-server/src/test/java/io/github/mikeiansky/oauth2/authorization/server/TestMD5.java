package io.github.mikeiansky.oauth2.authorization.server;

import cn.hutool.crypto.digest.MD5;

import java.security.MessageDigest;
import java.util.Date;

/**
 * @author mike ian
 * @date 2025/4/23
 * @desc
 **/
public class TestMD5 {

    public static String jdkMD5(String value){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff)); // 保证两位
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 加密出错", e);
        }
    }

    public static void main(String[] args) {

        String tag = "hello";
        System.out.println(jdkMD5(tag));
        System.out.println(new Date());
        MD5 md5 = MD5.create();
        for (int i=0;i<1000_000;i++) {
//            md5.digestHex(tag);
            MD5.create().digestHex(tag);
//            System.out.println(MD5.create().digestHex(tag));
        }
        System.out.println(new Date());

    }

}
