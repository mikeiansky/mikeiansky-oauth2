package io.github.mikeiansky.oauth2.authorization.server;

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.filter.ExtraProcessor;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mike ian
 * @date 2025/4/25
 * @desc
 **/
public class TestFastJson {

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "hello");
        map.put("exp", Instant.now());

        System.out.println(JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(map, JSONWriter.Feature.WriteClassName));
    }

}
