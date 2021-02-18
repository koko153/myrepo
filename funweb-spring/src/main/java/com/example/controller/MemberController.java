package com.example.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.domain.MemberVo;
import com.example.service.MemberService;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping("/member/*")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
//	
//	public void setMemberService(MemberService memberService) {
//		this.memberService = memberService;
//	}


	//	@RequestMapping(value = "/join", method = RequestMethod.GET)
	@GetMapping("/join")
	public void join() {
		log.info("GET - join() 호출됨");
//		return "member/join";   // 메소드 리턴타입이 String일 경우
	}
	
	
	@PostMapping("/join")
	public String join(MemberVo memberVo) {
		log.info("POST - join() 호출됨");
		
		// 사용자 입력 패스워드를 암호화된 문자열로 변경
		String passwd = memberVo.getPasswd();
		String hashedPwd = BCrypt.hashpw(passwd, BCrypt.gensalt());
		memberVo.setPasswd(hashedPwd);
		
		// 회원가입 날짜 설정
		memberVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		log.info("memberVo : " + memberVo);
		
		// 회원가입 처리
		memberService.addMember(memberVo);
		
		return "redirect:/member/login";
	}
	
	
	@GetMapping("/joinIdDupCheck")
	public String joinIdDupCheck(String id, Model model) {
		log.info("id : " + id);
		
		int count = memberService.getCountById(id);
		
		// Model 타입 객체에 뷰(JSP)에서 사용할 데이터를 저장하기
		model.addAttribute("id", id);
		model.addAttribute("count", count);
		
		return "member/joinIdDupCheck";
	} // joinIdDupCheck
	
	
	@GetMapping(value = "/ajax/joinIdDupChk", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody // 리턴 객체를 JSON 문자열로 변환해서 응답을 줌
	public Map<String, Boolean> ajaxJoinIdDupChk(String id) {
		
		int count = memberService.getCountById(id);

		Map<String, Boolean> map = new HashMap<>();
		if (count == 0) {
			map.put("isIdDup", false);
		} else { // count == 1
			map.put("isIdDup", true);
		}
		
		return map;
	}
	
	
	@GetMapping("/login")
	public void login() {
//		return "member/login";
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(String id, String passwd, 
			@RequestParam(defaultValue = "false") boolean keepLogin,
			HttpSession session,
			HttpServletResponse response) {
		
		int check = memberService.userCheck(id, passwd);
		
		// 로그인 실패시
		if (check != 1) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html; charset=UTF-8");
			
			StringBuilder sb = new StringBuilder();
			sb.append("<script>");
			sb.append("  alert('아이디 또는 패스워드가 일치하지 않습니다.');");
			sb.append("  history.back();");
			sb.append("</script>");
			
			return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
		}
		
		// 로그인 성공시
		// 세션에 아이디 저장(로그인 인증)
		session.setAttribute("id", id);
		
		if (keepLogin) { // keepLogin == true
			Cookie cookie = new Cookie("id", id);
			cookie.setMaxAge(60 * 10);  // 쿠키 유효시간 10분
			cookie.setPath("/");

			response.addCookie(cookie);
		}
		
//		return "redirect:/";
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/"); // 리다이렉트 경로를 Location으로 설정
		// 리다이렉트일 경우는 HttpStatus.FOUND 를 지정해야 함
		return new ResponseEntity<String>(headers, HttpStatus.FOUND);
	} // login
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		// 세션 초기화
		session.invalidate();
		
		// 로그인 상태유지용 쿠키가 존재하면 삭제
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("id")) {
					cookie.setMaxAge(0); // 유효시간 0
					cookie.setPath("/"); // 경로는 생성할때와 동일하게 설정해야 삭제됨
					
					response.addCookie(cookie); // 삭제할 쿠키정보를 추가
				}
			}
		}
		
		return "redirect:/";
	} // logout
	
}









