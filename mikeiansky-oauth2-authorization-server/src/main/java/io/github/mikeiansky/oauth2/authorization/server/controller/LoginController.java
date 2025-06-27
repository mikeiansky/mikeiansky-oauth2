package io.github.mikeiansky.oauth2.authorization.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
@Slf4j
@Controller
public class LoginController {

    @GetMapping("login")
    public String loginPage(){
        log.info("welcome to hello page ... ");
        return "login";
    }

}
