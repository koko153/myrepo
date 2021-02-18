package com.example;

import java.util.Timer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

@Configuration
public class MyConfig {

    // 스프링이 @Configuration 클래스의 @Bean 메소드를 자동호출해서
	// 메소드로부터 리턴받은 객체를 스프링 빈으로 등록해줌.
	// 기본생성자 호출로 준비할수 없는 스프링 빈들은 이 방식으로 준비함. 
	@Bean
	public Timer timer() {
		return new Timer(true);
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}
	
//	@Bean
//	public BCryptPasswordEncoder bCryptPasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
}
