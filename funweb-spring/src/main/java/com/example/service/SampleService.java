package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {
	
	public Integer doAdd(String str1, String str2) throws NumberFormatException {
		return Integer.parseInt(str1) + Integer.parseInt(str2);
	}
}

/*
  // 프록시(대리자) 클래스가 실행중에 자동 생성되어 타깃(SampleService) 타입 스프링 빈 객체로 대신 등록됨
  // @Autowired 로 의존객체를 받을때 SampleService 가 아니라 SampleService$$EnhancerBySpringCGLIB$$e396fae9 를 주입받게 됨
  // 포인트컷 표현식으로 타깃이 된 모든 클래스는 이와같이 실행중에 모두 프록시 클래스 객체로써 동작하게 됨.
@Component
class SampleService$$EnhancerBySpringCGLIB$$e396fae9 extends SampleService {
	
	@Autowired
	private SampleService sampleService; // 주요 로직
	@Autowired
	private LogAdvice logAdvice; // 보조(주변주) 로직
	
	@Override
	public Integer doAdd(String str1, String str2) {
		// 주요로직 호출 전에 전처리 작업
		logAdvice.logBefore(); // 주변부 로직 호출
		
		int result = sampleService.doAdd(str1, str2); // 주요 로직 호출
		
		// 주요로직 호출 후에 후처리 작업
		return result;
	}
}
*/








