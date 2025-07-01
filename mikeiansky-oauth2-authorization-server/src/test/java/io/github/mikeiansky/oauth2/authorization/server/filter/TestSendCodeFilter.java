package io.github.mikeiansky.oauth2.authorization.server.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 *
 * @author mike ian
 * @date 2025/7/1
 * @desc
 **/
@ActiveProfiles(profiles = "ciwei")
@AutoConfigureMockMvc
@AutoConfigureWebTestClient(timeout = "PT10M")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSendCodeFilter {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testSendCode() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/passport/send-code");

        try {
            MvcResult mvcResult = mvc.perform(requestBuilder)
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
