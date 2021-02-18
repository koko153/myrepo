package com.example.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync // ��� ������ �޼ҵ带 ���ķ� ȣ����
@Slf4j
public class MyScheduleTask {
	
	@Autowired
	private NoticeService noticeService;

	@Scheduled(fixedRate = 1000 ) // �и��� �� �ֱ�� �ݺ�����
	public void scheduleTask1() {
		long now = System.currentTimeMillis() / 1000;
		log.info("������ �۾�1 : 1�ʸ��� ���� - {}", now);
	}
	
	@Scheduled(fixedRate = 3000 )
	public void scheduleTask2() {
		long now = System.currentTimeMillis() / 1000;
		log.info("������ �۾�2 : 3�ʸ��� ���� - {}", now);
	}
	
	// �� �� �� �� �� ���� (�⵵)
	@Scheduled(cron = "0,30 * * * * ?")
	public void scheduleTask3() {
		long now = System.currentTimeMillis() / 1000;
		log.info("������ �۾�3 : �ź� 0��, 30�� ������ ���� - {}", now);
	}
	
}
