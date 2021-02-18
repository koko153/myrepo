package com.example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


// 회원 로그인 체크 용도의 인터셉터 클래스 정의
@Component
public class MemberLoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 컨트롤러의 특정 메소드 호출시 그것보다 먼저 실행됨
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("id");
		if (id != null) {  // 로그인 했을때는
			return true;   // true를 리턴하여 해당 컨트롤러 메소드 실행함
		}
		// 로그인 안했을때는 로그인 화면으로 리다이렉트 이동
		response.sendRedirect("/member/login");
		return false;  // false를 리턴하여 해당 컨트롤러 메소드 실행 안함
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// 컨트롤러의 특정 메소드 호출시 컨트롤러 메소드 호출완료 이후에 실행됨
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 컨트롤러의 특정 메소드 호출시 컨트롤러 메소드 호출완료하고 리턴한 jsp 뷰 실행완료후 실행됨
	}
}




