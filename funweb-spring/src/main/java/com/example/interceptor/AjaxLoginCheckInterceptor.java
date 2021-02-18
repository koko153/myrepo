package com.example.interceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;


// ȸ�� �α��� üũ �뵵�� ���ͼ��� Ŭ���� ����
@Component
public class AjaxLoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("id");
		if (id != null) {  // �α��� ��������
			return true;   // true�� �����Ͽ� �ش� ��Ʈ�ѷ� �޼ҵ� ������
		}
		// �α��� ���������� HTTP �����ڵ� ����(500)�� �Բ� JSON ���ڿ��� �������� ��
		Map<String, Object> map = new HashMap<>();
		map.put("isLogin", false);
		
		Gson gson = new Gson();
		String strJson = gson.toJson(map);
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(strJson);
		//out.flush();
		out.close();
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




