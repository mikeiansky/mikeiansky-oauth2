package io.github.mikeiansky.oauth2.authorization.server;

import cn.hutool.jwt.JWT;
import com.alibaba.fastjson2.JSON;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestJwt {

    public static void generateJwtByHutool(){
        JWT jwt = new JWT();
        jwt.addHeaders(Map.of("alg", "HS256"));
        jwt.addPayloads(Map.of("name", "ian"));
        jwt.setSigner("HS256", "test".getBytes());
        System.out.println(jwt.sign());
    }

    public static void generateJwtBySpring(){

//        RSAKey rsaKey = null;
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        ImmutableJWKSet jwkSource = new ImmutableJWKSet(jwkSet);
//        NimbusJwtEncoder jwtEncoderTemp = new NimbusJwtEncoder(jwkSource);

        // ✅ Step 1: 创建 HMAC SecretKey
        String secret = "my-super-secret-key-which-is-long-enough";
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        // ✅ Step 2: 创建对称加密的 JwtEncoder
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));

        String tokenValue = "hello";
        Instant issuedAt = Instant.now();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", tokenValue);
        claims.put("iat", issuedAt.toEpochMilli());
        claims.put("name", "ian");
//        Jwt jwt = new Jwt(tokenValue, issuedAt, expiresAt, headers, claims);

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256)
                .header("test_header", "header_value_001")
                .build();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .audience(List.of("001", "002"))
                .issuedAt(Instant.now())
                .expiresAt(expiresAt)
                .id("id-001-002")
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);



        Jwt jwt = jwtEncoder.encode(parameters);
        System.out.println(jwt.getTokenValue());

    }

    public static void main(String[] args) {
        generateJwtBySpring();
//        generateJwtByHutool();
    }

}
