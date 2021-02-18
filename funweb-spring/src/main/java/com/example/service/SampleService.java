package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {
	
	public Integer doAdd(String str1, String str2) throws NumberFormatException {
		return Integer.parseInt(str1) + Integer.parseInt(str2);
	}
}

/*
  // ���Ͻ�(�븮��) Ŭ������ �����߿� �ڵ� �����Ǿ� Ÿ��(SampleService) Ÿ�� ������ �� ��ü�� ��� ��ϵ�
  // @Autowired �� ������ü�� ������ SampleService �� �ƴ϶� SampleService$$EnhancerBySpringCGLIB$$e396fae9 �� ���Թް� ��
  // ����Ʈ�� ǥ�������� Ÿ���� �� ��� Ŭ������ �̿Ͱ��� �����߿� ��� ���Ͻ� Ŭ���� ��ü�ν� �����ϰ� ��.
@Component
class SampleService$$EnhancerBySpringCGLIB$$e396fae9 extends SampleService {
	
	@Autowired
	private SampleService sampleService; // �ֿ� ����
	@Autowired
	private LogAdvice logAdvice; // ����(�ֺ���) ����
	
	@Override
	public Integer doAdd(String str1, String str2) {
		// �ֿ���� ȣ�� ���� ��ó�� �۾�
		logAdvice.logBefore(); // �ֺ��� ���� ȣ��
		
		int result = sampleService.doAdd(str1, str2); // �ֿ� ���� ȣ��
		
		// �ֿ���� ȣ�� �Ŀ� ��ó�� �۾�
		return result;
	}
}
*/








