package io.github.mikeiansky.oauth2.authorization.server.controller;

import io.github.mikeiansky.oauth2.authorization.server.model.common.RespResult;
import io.github.mikeiansky.oauth2.authorization.server.model.dto.SendSmsCodeDTO;
import io.github.mikeiansky.oauth2.authorization.server.model.vo.About;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
@Slf4j
@RequestMapping("passport")
@Controller
public class PassportController {


    @GetMapping("login")
    public String login() {
        return "login";
    }

    @ResponseBody
    @PostMapping("send-code")
    public RespResult<String> sendCode(@Valid @RequestBody SendSmsCodeDTO sendCodeDTO) {
        return RespResult.ok("10086");
    }

    @ResponseBody
    public RespResult<String> loginWithMobile() {
        return RespResult.ok("10086");
    }

    @ResponseBody
    @GetMapping("about")
    public About about() {
        About about = new About();
        about.setVersion("1.0");
        about.setName("Mikeiansky");
        return about;
    }

}
