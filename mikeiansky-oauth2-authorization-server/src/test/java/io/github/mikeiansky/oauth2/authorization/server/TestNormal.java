package io.github.mikeiansky.oauth2.authorization.server;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Set;

/**
 * @author mike ian
 * @date 2025/4/22
 * @desc
 **/
public class TestNormal {

    public static void main(String[] args) {
        Instant instant = Instant.now();
        System.out.println(instant);

        Instant instant2 = instant.plus(60, ChronoUnit.MINUTES);
        System.out.println(instant2);
        System.out.println(instant.getEpochSecond());
        System.out.println(instant2.getEpochSecond());
        System.out.println(instant2.getEpochSecond() - instant.getEpochSecond());

    }

}
