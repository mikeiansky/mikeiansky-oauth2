package io.github.mikeiansky.oauth2.authorization.server.obj;

import com.alibaba.fastjson2.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mike ian
 * @date 2025/4/25
 * @desc
 **/
public class TestFastJson {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class One {
        private String name;
        private String value;
    }

    public static void main(String[] args) {
        One one = new One("one", "two");
        System.out.println(JSON.toJSONString(one));
    }

}
