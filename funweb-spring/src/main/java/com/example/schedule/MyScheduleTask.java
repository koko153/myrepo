package com.example.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync // 모든 스케줄 메소드를 병렬로 호출함
@Slf4j
public class MyScheduleTask {
	
	@Autowired
	private NoticeService noticeService;

	@Scheduled(fixedRate = 1000 ) // 밀리초 값 주기로 반복실행
	public void scheduleTask1() {
		long now = System.currentTimeMillis() / 1000;
		log.info("스케줄 작업1 : 1초마다 수행 - {}", now);
	}
	
	@Scheduled(fixedRate = 3000 )
	public void scheduleTask2() {
		long now = System.currentTimeMillis() / 1000;
		log.info("스케줄 작업2 : 3초마다 수행 - {}", now);
	}
	
	// 초 분 시 일 월 요일 (년도)
	@Scheduled(cron = "0,30 * * * * ?")
	public void scheduleTask3() {
		long now = System.currentTimeMillis() / 1000;
		log.info("스케줄 작업3 : 매분 0초, 30초 때마다 수행 - {}", now);
	}
	
}
