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
 AOP(���� ���� ���α׷���) ��� ����
- Target(Ÿ��) : �ٽ� ����Ͻ�(�ֿ�) ������ ������ ��ü. (��: SampleService ��ü)
- Proxy(���Ͻ�) : ���Ͻô� ���������� Ÿ���� �����ؼ� ȣ���ϴ� ��ü. ������������ �����߿� ���Ͻð� �ڵ����� �������(Auto Proxy ���)
- JoinPoint(��������Ʈ) : Ÿ�� ��ü�� ���� ���� �޼ҵ��(�ĺ� �޼ҵ��). �����̽��� ���� ������ ������ �ǹ���.
- PointCut(����Ʈ��) : ��������Ʈ(�ĺ� �޼ҵ��) �߿��� ����Ʈ�� ǥ�������� [����]�� �޼ҵ�. �����̽��� ������ ����Ǵ� ��������Ʈ�� �ǹ���.
- Weaving(����) : �����̽�(��������)�� �ٽ� �����ڵ�(�ֿ����)�� �����ϴ� �۾��� �ǹ���.
- Advice(�����̽�) : ���������� ���� Ŭ����. Aspect�� ���� �ҽ��ڵ�� ������ Ŭ������ �ǹ���. (��: LogAdvice Ŭ����)
- Aspect(�ֽ���Ʈ) : �ֺ��� ���ɻ�(��������)�� �ǹ��ϴ� �߻���. ���� ���� Ŭ������ �ǹ����� ����. 
*/

//@Aspect // �ֺ��� ���ɻ�(����) ������ ���� Ŭ����. Ŭ���� ������ AOP��� �Ѱ� ���� ����ġ ����.
@Component
@Slf4j
public class LogAdvice {

	// ����Ʈ�� ǥ����
	@Before("execution(public * com.example.service.SampleService.*(..) )")
	public void logBefore() {
		log.info("================ @Before - logBefore() ȣ��� ================");
	}
	
	@Before("execution(public * com.example.service.SampleService.doAdd(String, String)) && args(str1, str2)")
	public void logBeforeWithParam(String str1, String str2) {
		log.info("================ @Before - logBeforeWithParam() ȣ��� ================");
		log.info("str1 : {}", str1);
		log.info("str2 : {}", str2);
	}
	
	@AfterReturning("execution(Integer com.example.service.SampleService.doAdd(String, String) )")
	public void logAfterReturning() {
		log.info("================ @AfterReturning - logAfterReturning() ȣ��� ================");
	}
	
	@AfterThrowing(pointcut = "execution(Integer com.example.service.SampleService.doAdd(String, String) )", throwing = "exception")
	public void logAfterThrowing(Exception exception) {
		log.info("================ @AfterThrowing - logAfterThrowing() ȣ��� ================");
		log.info("Exception : {}", exception.getMessage());
	}
	
	@After("execution(* do*(*, *) )")
	public void logAfter() {
		log.info("================ @After - logAfter() ȣ��� ================");
	}
	
	@Around("execution(* com.example..*.*(..) )")
	public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
		log.info("================ @Around - logTime() ȣ��� ================");
		
		Signature signature = pjp.getSignature();
		log.info("Ÿ�� ��ü�� �޼ҵ� �̸�: {}", signature.getName());
		log.info("Ÿ�� ��ü�� �޼ҵ� Ǯ����: {}", signature.toLongString());
		log.info("Ÿ�� ��ü�� �޼ҵ� ��Ʈ����: {}", signature.toShortString());
		log.info("��� Ÿ�� ��ü: {}", pjp.getTarget());
		
		Object[] arrArgs = pjp.getArgs();  // Ÿ�� �޼ҵ忡 ���޵� �Ű����� ���� �迭��ü�� ��������
		String strArgs = Arrays.toString(arrArgs);
		log.info("��� Ÿ�� ��ü �޼ҵ��� �Ű����� ���: {}", strArgs);
		
		long beginTime = System.currentTimeMillis();
		// proceed() �޼ҵ�� Ÿ�� ��ü�� �޼ҵ带 ȣ����
		// ȣ�� ����� Ÿ�� �޼ҵ��� ��ȯ���� ������
		// Ÿ�� �޼ҵ��� ��ȯŸ���� void �� ��� null�� ������
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			// ���ܹ߻��� ��ó���ϴ� @AfterThrowing �� �ش��ϴ� �κ�
			log.info("Exception : {}", e.getMessage());
		}
		log.info("result : {}", result);
		
		long endTime = System.currentTimeMillis();
		long execTime = endTime - beginTime; // �޼ҵ� ����ð� �и���
		log.info("�޼ҵ��: {}, ����ð�: {}", signature.toShortString(), execTime + "ms");
		
		return result;
	}
}

