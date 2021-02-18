package com.example;

import java.util.Timer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

@Configuration
public class MyConfig {

    // �������� @Configuration Ŭ������ @Bean �޼ҵ带 �ڵ�ȣ���ؼ�
	// �޼ҵ�κ��� ���Ϲ��� ��ü�� ������ ������ �������.
	// �⺻������ ȣ��� �غ��Ҽ� ���� ������ ����� �� ������� �غ���. 
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
