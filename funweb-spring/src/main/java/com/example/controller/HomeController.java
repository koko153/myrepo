package com.example.controller;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.java.Log;

@Controller // Ŭ���� �ȿ��� @GetMapping ���� �ֳ����̼��� ��� ����
@Log
public class HomeController  {
	
	@Autowired
	private Timer timer;

	@GetMapping("/")
	public String index() {
		log.info("index() ȣ���");
		return "index";
	}
	
	@GetMapping("/company/welcome")
	public void welcome() {
		log.info("welcome() ȣ���");
//		return "company/welcome";
		
		// ����Ÿ���� void�� �ֳ����̼� url ��û��θ�
		// ������ jsp�� �̸����� �����
	}
	
	@GetMapping("/company/history")
	public void history() {
		log.info("history() ȣ���");
	}
	
	
	
}
