package io.github.mikeiansky.oauth2.authorization.server.obj;

import com.alibaba.fastjson2.JSON;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 *
 * @author mike ian
 * @date 2025/6/26
 * @desc
 **/
public class TestAuthentication {

    public static void main(String[] args) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("mikeiansky", "mikeiansky");
        authentication.setAuthenticated(true);
        authentication.setDetails(null);
        System.out.println(JSON.toJSONString(authentication));
        System.out.println(authentication.getDetails());

    }

}
