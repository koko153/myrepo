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
		log.info("GET - join() ȣ���");
//		return "member/join";   // �޼ҵ� ����Ÿ���� String�� ���
	}
	
	
	@PostMapping("/join")
	public String join(MemberVo memberVo) {
		log.info("POST - join() ȣ���");
		
		// ����� �Է� �н����带 ��ȣȭ�� ���ڿ��� ����
		String passwd = memberVo.getPasswd();
		String hashedPwd = BCrypt.hashpw(passwd, BCrypt.gensalt());
		memberVo.setPasswd(hashedPwd);
		
		// ȸ������ ��¥ ����
		memberVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		log.info("memberVo : " + memberVo);
		
		// ȸ������ ó��
		memberService.addMember(memberVo);
		
		return "redirect:/member/login";
	}
	
	
	@GetMapping("/joinIdDupCheck")
	public String joinIdDupCheck(String id, Model model) {
		log.info("id : " + id);
		
		int count = memberService.getCountById(id);
		
		// Model Ÿ�� ��ü�� ��(JSP)���� ����� �����͸� �����ϱ�
		model.addAttribute("id", id);
		model.addAttribute("count", count);
		
		return "member/joinIdDupCheck";
	} // joinIdDupCheck
	
	
	@GetMapping(value = "/ajax/joinIdDupChk", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody // ���� ��ü�� JSON ���ڿ��� ��ȯ�ؼ� ������ ��
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
		
		// �α��� ���н�
		if (check != 1) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html; charset=UTF-8");
			
			StringBuilder sb = new StringBuilder();
			sb.append("<script>");
			sb.append("  alert('���̵� �Ǵ� �н����尡 ��ġ���� �ʽ��ϴ�.');");
			sb.append("  history.back();");
			sb.append("</script>");
			
			return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
		}
		
		// �α��� ������
		// ���ǿ� ���̵� ����(�α��� ����)
		session.setAttribute("id", id);
		
		if (keepLogin) { // keepLogin == true
			Cookie cookie = new Cookie("id", id);
			cookie.setMaxAge(60 * 10);  // ��Ű ��ȿ�ð� 10��
			cookie.setPath("/");

			response.addCookie(cookie);
		}
		
//		return "redirect:/";
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/"); // �����̷�Ʈ ��θ� Location���� ����
		// �����̷�Ʈ�� ���� HttpStatus.FOUND �� �����ؾ� ��
		return new ResponseEntity<String>(headers, HttpStatus.FOUND);
	} // login
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		// ���� �ʱ�ȭ
		session.invalidate();
		
		// �α��� ���������� ��Ű�� �����ϸ� ����
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("id")) {
					cookie.setMaxAge(0); // ��ȿ�ð� 0
					cookie.setPath("/"); // ��δ� �����Ҷ��� �����ϰ� �����ؾ� ������
					
					response.addCookie(cookie); // ������ ��Ű������ �߰�
				}
			}
		}
		
		return "redirect:/";
	} // logout
	
}









