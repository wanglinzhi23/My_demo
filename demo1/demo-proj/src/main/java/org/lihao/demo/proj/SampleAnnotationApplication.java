package org.lihao.demo.proj;

import org.lihao.demo.core.annotations.EnableDemoCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDemoCore
public class SampleAnnotationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleAnnotationApplication.class, args);
	}

}
