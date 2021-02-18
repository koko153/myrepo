package com.example.controller;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.java.Log;

@Controller // 클래스 안에서 @GetMapping 등의 애노테이션을 사용 가능
@Log
public class HomeController  {
	
	@Autowired
	private Timer timer;

	@GetMapping("/")
	public String index() {
		log.info("index() 호출됨");
		return "index";
	}
	
	@GetMapping("/company/welcome")
	public void welcome() {
		log.info("welcome() 호출됨");
//		return "company/welcome";
		
		// 리턴타입이 void면 애노테이션 url 요청경로를
		// 실행할 jsp뷰 이름으로 사용함
	}
	
	@GetMapping("/company/history")
	public void history() {
		log.info("history() 호출됨");
	}
	
	
	
}
