package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/*
@SpringBootApplication 애노테이션이 수행하는 일
: 이 애노테이션이 달린 클래스의 패키지를 기준으로
  @Component 계열( @Configuration, @Controller, @RestController, @Service, @Repository 등 )
  애노테이션이 달린 클래스들을 하위 패키지까지 스캔해서
  스프링이 관리하는 객체(스프링 빈 또는 빈이라고 부름)로 등록함 -> 의존관계 주입(DI) 됨
*/
@SpringBootApplication  // 프로젝트 내의 @Component 계열 애노테이션이 붙은 클래스를 스프링 빈으로 등록해줌
@MapperScan("com.example.mapper")  // 마이바티스 애노테이션
//@EnableScheduling  // 프로젝트 내의 @Schedule 붙은 클래스들을 스캔해서 스케줄에 맞게 호출해줌
//@EnableAspectJAutoProxy // 스프링부트에서 기본 활성화로 정책 변경되서 생략함. 스캔에 의해 스프링 빈이 된 객체중에 @Aspect 붙은 객체들을 AOP 용도로 등록해서 처리
@EnableWebSocket  // 웹소켓 서버 기능을 활성하기
public class FunwebSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunwebSpringApplication.class, args);
	}
}




