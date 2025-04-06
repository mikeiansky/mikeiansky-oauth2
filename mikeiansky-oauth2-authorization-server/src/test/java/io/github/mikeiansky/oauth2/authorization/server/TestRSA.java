package io.github.mikeiansky.oauth2.authorization.server;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TestRSA {

    public static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    public static String convertToPemFormat(String type, byte[] encoded) {
        String base64Encoded = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" + base64Encoded + "\n-----END " + type + "-----";
    }

    public static PublicKey loadPublicKey(String pem) throws Exception {
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadPrivateKey(String pem) throws Exception {
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = generateRsaKey();
        System.out.println(keyPair.getPrivate().getClass());
        System.out.println(keyPair.getPrivate());
        System.out.println(keyPair.getPrivate().getAlgorithm());
        System.out.println(keyPair.getPrivate().getFormat());
//        System.out.println(keyPair.getPrivate().getEncoded());
        System.out.println(" -------------------------------- ");
        System.out.println(keyPair.getPublic().getClass());
        System.out.println(keyPair.getPublic());
        System.out.println(keyPair.getPublic().getAlgorithm());
        System.out.println(keyPair.getPublic().getFormat());
        System.out.println(" ++++++++++++++++++++++++++++++++ ");
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        RSAKey key = new RSAKey();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        System.out.println(rsaKey );
        System.out.println(rsaKey.toPublicJWK());

        byte[] publicEncoded = publicKey.getEncoded();
        byte[] privateEncoded = privateKey.getEncoded();
        String publicKeyPem = convertToPemFormat("PUBLIC KEY", publicEncoded);
        String privateKeyPem = convertToPemFormat("PRIVATE KEY", privateEncoded);

        System.out.println("===== PUBLIC KEY =====");
        System.out.println(publicKeyPem);
        System.out.println("\n===== PRIVATE KEY =====");
        System.out.println(privateKeyPem);


        PublicKey publicKeyRead = loadPublicKey(publicKeyPem);
        System.out.println("========= read public key =======");
        System.out.println(publicKeyRead);
        System.out.println("========= read private key =======");
        PrivateKey privateKeyRead = loadPrivateKey(privateKeyPem);
        System.out.println(privateKeyRead);

    }

}
