package com.example.demo.config;

import com.example.demo.Util.MD5Util;
import com.example.demo.service.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: WebSecurityConfig.java
 * @Package com.example.demo.config
 * @Description
 * @date 2018 05-10 17:09.
 */
@Configuration //必须加这个注解，用于生成一个配置类，
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true) //启用Security注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	UserDetailsService customUserService() { // 注册UserDetailsService 的bean
		return new CustomUserService();

	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserService())
				.passwordEncoder(new PasswordEncoder(){
			//使用MD5获取加密之后的密码
			@Override
			public String encode(CharSequence rawPassword) {
				return MD5Util.encode((String)rawPassword);
			}
			//验证密码
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equals(MD5Util.encode((String)rawPassword));
			}})
		; //user Details Service验证
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//.antMatchers("/").permitAll()  //首页任意访问
				.anyRequest().authenticated() // //其他所有资源都需要认证，登陆后才能访问
				.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)//登录成功之后跳转首页
				.failureUrl("/login?error") //登录失败 返回error
				.permitAll() // 登录页面用户任意访问
				.and()
				.logout().permitAll(); // 注销行为任意访问

	}
}
