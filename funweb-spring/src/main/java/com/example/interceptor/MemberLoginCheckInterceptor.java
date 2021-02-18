package com.example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


// ȸ�� �α��� üũ �뵵�� ���ͼ��� Ŭ���� ����
@Component
public class MemberLoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// ��Ʈ�ѷ��� Ư�� �޼ҵ� ȣ��� �װͺ��� ���� �����
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("id");
		if (id != null) {  // �α��� ��������
			return true;   // true�� �����Ͽ� �ش� ��Ʈ�ѷ� �޼ҵ� ������
		}
		// �α��� ���������� �α��� ȭ������ �����̷�Ʈ �̵�
		response.sendRedirect("/member/login");
		return false;  // false�� �����Ͽ� �ش� ��Ʈ�ѷ� �޼ҵ� ���� ����
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// ��Ʈ�ѷ��� Ư�� �޼ҵ� ȣ��� ��Ʈ�ѷ� �޼ҵ� ȣ��Ϸ� ���Ŀ� �����
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// ��Ʈ�ѷ��� Ư�� �޼ҵ� ȣ��� ��Ʈ�ѷ� �޼ҵ� ȣ��Ϸ��ϰ� ������ jsp �� ����Ϸ��� �����
	}
}




