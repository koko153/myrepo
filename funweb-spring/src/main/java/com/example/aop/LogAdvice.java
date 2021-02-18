package com.example.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
 AOP(관점 지향 프로그래밍) 용어 정리
- Target(타깃) : 핵심 비즈니스(주요) 로직을 가지는 객체. (예: SampleService 객체)
- Proxy(프록시) : 프록시는 내부적으로 타깃을 포함해서 호출하는 객체. 스프링에서는 실행중에 프록시가 자동으로 만들어짐(Auto Proxy 방식)
- JoinPoint(조인포인트) : 타깃 객체가 가진 여러 메소드들(후보 메소드들). 어드바이스가 적용 가능한 지점을 의미함.
- PointCut(포인트컷) : 조인포인트(후보 메소드들) 중에서 포인트컷 표현식으로 [결정]된 메소드. 어드바이스가 실제로 적용되는 조인포인트를 의미함.
- Weaving(위빙) : 어드바이스(보조로직)를 핵심 로직코드(주요로직)에 적용하는 작업을 의미함.
- Advice(어드바이스) : 보조로직을 가진 클래스. Aspect를 실제 소스코드로 구현한 클래스를 의미함. (예: LogAdvice 클래스)
- Aspect(애스펙트) : 주변부 관심사(보조로직)을 의미하는 추상명사. 실제 구현 클래스를 의미하지 않음. 
*/

//@Aspect // 주변부 관심사(보조) 로직을 가진 클래스. 클래스 단위로 AOP기능 켜고 끄는 스위치 역할.
@Component
@Slf4j
public class LogAdvice {

	// 포인트컷 표현식
	@Before("execution(public * com.example.service.SampleService.*(..) )")
	public void logBefore() {
		log.info("================ @Before - logBefore() 호출됨 ================");
	}
	
	@Before("execution(public * com.example.service.SampleService.doAdd(String, String)) && args(str1, str2)")
	public void logBeforeWithParam(String str1, String str2) {
		log.info("================ @Before - logBeforeWithParam() 호출됨 ================");
		log.info("str1 : {}", str1);
		log.info("str2 : {}", str2);
	}
	
	@AfterReturning("execution(Integer com.example.service.SampleService.doAdd(String, String) )")
	public void logAfterReturning() {
		log.info("================ @AfterReturning - logAfterReturning() 호출됨 ================");
	}
	
	@AfterThrowing(pointcut = "execution(Integer com.example.service.SampleService.doAdd(String, String) )", throwing = "exception")
	public void logAfterThrowing(Exception exception) {
		log.info("================ @AfterThrowing - logAfterThrowing() 호출됨 ================");
		log.info("Exception : {}", exception.getMessage());
	}
	
	@After("execution(* do*(*, *) )")
	public void logAfter() {
		log.info("================ @After - logAfter() 호출됨 ================");
	}
	
	@Around("execution(* com.example..*.*(..) )")
	public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
		log.info("================ @Around - logTime() 호출됨 ================");
		
		Signature signature = pjp.getSignature();
		log.info("타깃 객체의 메소드 이름: {}", signature.getName());
		log.info("타깃 객체의 메소드 풀네임: {}", signature.toLongString());
		log.info("타깃 객체의 메소드 쇼트네임: {}", signature.toShortString());
		log.info("대상 타깃 객체: {}", pjp.getTarget());
		
		Object[] arrArgs = pjp.getArgs();  // 타깃 메소드에 전달된 매개변수 값을 배열객체로 가져오기
		String strArgs = Arrays.toString(arrArgs);
		log.info("대상 타깃 객체 메소드의 매개변수 목록: {}", strArgs);
		
		long beginTime = System.currentTimeMillis();
		// proceed() 메소드로 타깃 객체의 메소드를 호출함
		// 호출 결과로 타깃 메소드의 반환값을 리턴함
		// 타깃 메소드의 반환타입이 void 일 경우 null을 리턴함
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			// 예외발생시 후처리하는 @AfterThrowing 에 해당하는 부분
			log.info("Exception : {}", e.getMessage());
		}
		log.info("result : {}", result);
		
		long endTime = System.currentTimeMillis();
		long execTime = endTime - beginTime; // 메소드 수행시간 밀리초
		log.info("메소드명: {}, 실행시간: {}", signature.toShortString(), execTime + "ms");
		
		return result;
	}
}

