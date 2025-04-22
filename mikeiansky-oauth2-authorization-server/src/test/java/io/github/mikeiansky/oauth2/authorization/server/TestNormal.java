package io.github.mikeiansky.oauth2.authorization.server;

import java.util.Set;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
public class TestNormal {

    public static void main(String[] args) {
        Set set1 = Set.of("hello,world,test".split(","));
        System.out.println(set1);
        System.out.println(set1.size());
        Set set2 = Set.of(new String[]{"one","two","three"});
        System.out.println(set2);
        System.out.println(set2.size());

    }

}
