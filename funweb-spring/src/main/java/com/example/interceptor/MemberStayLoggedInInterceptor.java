package com.example.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


// ȸ�� �α��� ���� ���� �뵵�� ���ͼ��� Ŭ���� ����
@Component
public class MemberStayLoggedInInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// ��û ������� ���� ��������
		HttpSession session = request.getSession();
		// ���ǿ� �α��� ���̵� ������
		// �α��� ���������� ��Ű���� �����ͼ� ���ǿ� �����ϰ� �ش� ��Ʈ�ѷ� �޼ҵ� ȣ��
		String id = (String) session.getAttribute("id");
		if (id == null) {
			Cookie[] cookies = request.getCookies();
			// ��Ű name�� "id"�� ��Ű��ü ã��
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("id")) {
						id = cookie.getValue();
						// �α��� ���� ó��(���ǿ� id�� �߰�)
						session.setAttribute("id", id);
					}
				}
			}
		}
		// ���ǿ� �α��� ���̵� �̹� ������ �ٷ� �ش� ��Ʈ�ѷ� �޼ҵ� ȣ��
		return true;
	} // preHandle
}
