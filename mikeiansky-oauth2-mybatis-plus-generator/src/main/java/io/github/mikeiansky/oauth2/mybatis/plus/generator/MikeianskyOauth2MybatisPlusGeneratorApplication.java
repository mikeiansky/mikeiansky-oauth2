package io.github.mikeiansky.oauth2.mybatis.plus.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//@SpringBootApplication
public class MikeianskyOauth2MybatisPlusGeneratorApplication {

	public static void main(String[] args) {
//		ApplicationContext context = SpringApplication.run(MikeianskyOauth2MybatisPlusGeneratorApplication.class, args);
//		GeneratorService generatorService = context.getBean(GeneratorService.class);
//		generatorService.generator();

		GeneratorService generatorService = new GeneratorService();
		generatorService.generator();
	}

}
