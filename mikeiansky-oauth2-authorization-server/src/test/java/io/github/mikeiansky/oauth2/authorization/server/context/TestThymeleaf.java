package io.github.mikeiansky.oauth2.authorization.server.context;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 *
 * @author mike ian
 * @date 2025/7/1
 * @desc
 **/
@ActiveProfiles(profiles = "ciwei")
@SpringBootTest
public class TestThymeleaf {

    @Autowired
    private TemplateEngine templateEngine; // 注入 Thymeleaf 的模板引擎

    @Test
    public void testRender(){
        System.out.println("test render thymeleaf ..." + templateEngine);

//        _csrf.parameterName}" th:value="${_csrf.token

        // 1. 准备 Thymeleaf 的上下文数据
        Context context = new Context();
        context.setVariable("message", "Hello from Thymeleaf Test!");
        context.setVariable("now", java.time.LocalDateTime.now());
        JSONObject vv = new JSONObject();
        vv.put("parameterName", "parameterName");
        vv.put("token", "token");
        context.setVariable("_csrf", vv);

        // 2. 渲染模板（假设模板文件名为 test-page.html）
        String renderedHtml = templateEngine.process("login", context);
        System.out.println(renderedHtml);
    }

}
