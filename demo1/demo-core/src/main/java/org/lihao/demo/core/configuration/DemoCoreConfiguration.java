package org.lihao.demo.core.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lihao
 * @version V1.0
 * @Title: DemoCoreConfiguration.java
 * @Package org.lihao.demo.core.configuration
 * @Description Demo Core Configuration
 * @date 2018 04-10 14:20.
 */
@Configuration
@ComponentScan(basePackages = {
		"org.lihao.demo.core"
		,"org.lihao.demo.core.mapper"
		,"org.lihao.demo.core.controller"
		,"org.lihao.demo.core.service"
})
public class DemoCoreConfiguration {

}
