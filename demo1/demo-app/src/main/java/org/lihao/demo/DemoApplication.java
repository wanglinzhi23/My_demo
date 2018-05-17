package org.lihao.demo;

import org.lihao.demo.core.annotations.EnableDemoCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Lihao
 * @version V1.0
 * @Title: DemoApplication.java
 * @Package org.lihao.demo
 * @Description Demo Application
 * @date 2018 04-09 19:14.
 */
@SpringBootApplication
@EnableSwagger2
@EnableDemoCore
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
