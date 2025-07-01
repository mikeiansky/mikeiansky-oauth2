package io.github.mikeiansky.oauth2.authorization.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "ciwei")
@SpringBootTest
class MikeianskyOauth2AuthorizationServerApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("test contextLoads ...");
    }

}
