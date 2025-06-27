package io.github.mikeiansky.oauth2.authorization.server.obj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mike ian
 * @date 2025/6/27
 * @desc
 **/
public class TestMapPutIfAbsent {

    public static void main(String[] args) {

        Map<String, String> map = new HashMap<>();

        String v1 = map.putIfAbsent("key1", "value1");
        System.out.println(v1);
        String v2 = map.putIfAbsent("key1", "value1");
        System.out.println(v2);

        String v3 = map.computeIfAbsent("key1", k -> "value1");
        System.out.println(v3);

    }

}
