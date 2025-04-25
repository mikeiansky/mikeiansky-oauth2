package io.github.mikeiansky.oauth2.resource.server.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mike ian
 * @date 2025/4/7
 * @desc
 **/
@RestController
public class OAuth2ResourceServerController {

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Jwt jwt) {
        System.out.println("hello jwt");
        return String.format("Hello, %s!", jwt.getSubject());
    }

    @GetMapping("/hello")
    public String hello() {
        System.out.println("get hello api");
        return "hello resource server";
    }

    @GetMapping("/message")
    public String message() {
        System.out.println("hello message");
        return "secret message";
    }

    @PostMapping("/message")
    public String createMessage(@RequestBody String message) {
        System.out.println("create message");
        return String.format("Message was created. Content: %s", message);
    }

}
