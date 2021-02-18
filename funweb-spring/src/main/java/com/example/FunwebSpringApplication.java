package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/*
@SpringBootApplication �ֳ����̼��� �����ϴ� ��
: �� �ֳ����̼��� �޸� Ŭ������ ��Ű���� ��������
  @Component �迭( @Configuration, @Controller, @RestController, @Service, @Repository �� )
  �ֳ����̼��� �޸� Ŭ�������� ���� ��Ű������ ��ĵ�ؼ�
  �������� �����ϴ� ��ü(������ �� �Ǵ� ���̶�� �θ�)�� ����� -> �������� ����(DI) ��
*/
@SpringBootApplication  // ������Ʈ ���� @Component �迭 �ֳ����̼��� ���� Ŭ������ ������ ������ �������
@MapperScan("com.example.mapper")  // ���̹�Ƽ�� �ֳ����̼�
//@EnableScheduling  // ������Ʈ ���� @Schedule ���� Ŭ�������� ��ĵ�ؼ� �����ٿ� �°� ȣ������
//@EnableAspectJAutoProxy // ��������Ʈ���� �⺻ Ȱ��ȭ�� ��å ����Ǽ� ������. ��ĵ�� ���� ������ ���� �� ��ü�߿� @Aspect ���� ��ü���� AOP �뵵�� ����ؼ� ó��
@EnableWebSocket  // ������ ���� ����� Ȱ���ϱ�
public class FunwebSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunwebSpringApplication.class, args);
	}
}




