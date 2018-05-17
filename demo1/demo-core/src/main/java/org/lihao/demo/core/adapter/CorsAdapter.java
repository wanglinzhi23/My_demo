package org.lihao.demo.core.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 解决跨域问题
 */
@Configuration
public class CorsAdapter extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
          .allowCredentials(true).maxAge(3600);
    }
    
    
}