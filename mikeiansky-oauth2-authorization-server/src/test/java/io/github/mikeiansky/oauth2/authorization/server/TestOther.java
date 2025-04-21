package io.github.mikeiansky.oauth2.authorization.server;

import cn.hutool.core.codec.Hashids;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.util.IdUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author mike ian
 * @date 2025/4/2
 * @desc
 **/
public class TestOther {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "16CharSecretKeyn"; // 16字节密钥（生产中请放入配置）

    // 加密
    public static String encrypt(long id) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(String.valueOf(id).getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted); // URL安全
    }

    // 解密
    public static long decrypt(String encryptedId) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getUrlDecoder().decode(encryptedId);
        String decrypted = new String(cipher.doFinal(decoded));
        return Long.parseLong(decrypted);
    }

    public static void main(String[] args) throws Exception {
        long originalId = 1234567890L;
        String encrypted = encrypt(originalId);
        long decrypted = decrypt(encrypted);

        System.out.println("原始ID: " + originalId);
        System.out.println("加密后: " + encrypted);
        System.out.println("解密后: " + decrypted);
    }

}
