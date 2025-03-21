package io.github.mikeiansky.oauth2.authorization.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@MapperScan(basePackages = "io.github.mikeiansky.oauth2.authorization.server.mapper")
@SpringBootApplication
public class MikeianskyOauth2AuthorizationServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MikeianskyOauth2AuthorizationServerApplication.class, args);
        TestService testService = context.getBean(TestService.class);
        testService.test();
    }

}
