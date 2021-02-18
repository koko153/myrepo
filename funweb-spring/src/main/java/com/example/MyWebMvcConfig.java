package com.example;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.AjaxLoginCheckInterceptor;
import com.example.interceptor.MemberLoginCheckInterceptor;
import com.example.interceptor.MemberStayLoggedInInterceptor;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private  MemberLoginCheckInterceptor memberLoginCheckInterceptor;
	@Autowired
	private MemberStayLoggedInInterceptor memberStayLoggedInInterceptor;
	@Autowired
	private AjaxLoginCheckInterceptor ajaxLoginCheckInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// ȸ�� �α��� Ȯ�� ���ͼ��� ����ϱ�
		InterceptorRegistration registration = registry.addInterceptor(memberLoginCheckInterceptor);
		// ���ͼ��Ͱ� ����� URL �ּ� ��� �߰�
		//registration.addPathPatterns("/notice/write");
		registration.addPathPatterns("/notice/*");
		registration.addPathPatterns("/fileNotice/*");
		// ���ͼ��� ���࿡�� ������ URL �ּ� ��� �߰�
		registration.excludePathPatterns("/notice/list", "/notice/content");
		registration.excludePathPatterns("/fileNotice/list", "/fileNotice/content");
		
		// Ajax�� ȸ�� �α��� Ȯ�� ���ͼ��� ����ϱ�
		registry.addInterceptor(ajaxLoginCheckInterceptor)
		.addPathPatterns("/comment/*")
		.excludePathPatterns("/comment/one/*", "/comment/pages/*");
		
		// ȸ�� �α��� �������� ó�� ���ͼ��� ����ϱ�
		registry.addInterceptor(memberStayLoggedInInterceptor)
		.addPathPatterns("/*");
	} // addInterceptors
}



