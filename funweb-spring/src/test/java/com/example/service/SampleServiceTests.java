package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SampleServiceTests {

	@Autowired
	private SampleService sampleService;
	
	@Test
	void testSampleServiceClass() {
		log.info("정보1: {}", sampleService.toString());
		log.info("정보2: {}", sampleService.getClass().getName());
	}
	
	@Test
	void testDoAdd() {
		int result = sampleService.doAdd("123", "456");
		log.info("결과: {}", result);
		assertEquals(579, result);
	}
	
	@Test
	void testDoAddError() {
		
		Throwable exception = assertThrows(NumberFormatException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				int result = sampleService.doAdd("123", "ABC");
			}
		});
	}
	
	
}





