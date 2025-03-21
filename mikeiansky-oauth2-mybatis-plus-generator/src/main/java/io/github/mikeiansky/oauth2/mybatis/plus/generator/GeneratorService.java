package io.github.mikeiansky.oauth2.mybatis.plus.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@Service
public class GeneratorService {

    public void generator(){
        String outputDir = System.getProperty("user.dir") + File.separator
                + "mikeiansky-oauth2-mybatis-plus-generator"
                + "/src/main/java";
        String dbUrl = "jdbc:mysql://172.16.2.232:3306/mikeiansky_oauth2?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowMultiQueries=true&allowPublicKeyRetrieval=true";
        String dbUsername = "root";
        String dbPassword = "123456";

        FastAutoGenerator.create(dbUrl, dbUsername, dbPassword)
                .globalConfig(builder -> builder
                        .author("Mikeiansky")
                        .outputDir(outputDir)
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("io.github.mikeiansky.oauth2.model")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

    }

}
