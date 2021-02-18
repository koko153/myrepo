package com.example.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.domain.NoticeVo;
import com.example.domain.PageDto;
import com.example.service.MySqlService;
import com.example.service.NoticeService;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/notice/*")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private MySqlService mySqlService;
	
	@GetMapping("/list")
	public String list(
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "") String category,
			@RequestParam(defaultValue = "") String search,
			Model model) {
		
		//int count = noticeService.getCountAll();
		int count = noticeService.getCountBySearch(category, search);
		
		int pageSize = 10;
		
		int startRow = (pageNum - 1) * pageSize;		
		
		List<NoticeVo> noticeList = null;
		if (count > 0) {
			//noticeList = noticeService.getNotices(startRow, pageSize);
			noticeList = noticeService.getNoticesBySearch(startRow, pageSize, category, search);
		}
		
		
		PageDto pageDto = new PageDto();
		
		if (count > 0) {
			int pageCount = (count / pageSize) + (count % pageSize == 0 ? 0 : 1);
			//int pageCount = (int) Math.ceil((double) count / pageSize);
			
			int pageBlock = 5;
			
			// 1~5          6~10          11~15          16~20       ...
			// 1~5 => 1     6~10 => 6     11~15 => 11    16~20 => 16
			int startPage = ((pageNum / pageBlock) - (pageNum % pageBlock == 0 ? 1 : 0)) * pageBlock + 1;
			
			int endPage = startPage + pageBlock - 1;
			if (endPage > pageCount) {
				endPage = pageCount;
			}
			
			pageDto.setCategory(category);
			pageDto.setSearch(search);
			pageDto.setCount(count);
			pageDto.setPageCount(pageCount);
			pageDto.setPageBlock(pageBlock);
			pageDto.setStartPage(startPage);
			pageDto.setEndPage(endPage);
		} // if
		
		
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("pageDto", pageDto);
		model.addAttribute("pageNum", pageNum);
		
		return "center/notice";
	} // list
	
	
	@GetMapping("/write")
	public String write(@ModelAttribute("pageNum") String pageNum, HttpSession session, Model model) {
		// 로그인 안했을때는 글목록으로 리다이렉트 이동시킴
//		String id = (String) session.getAttribute("id");
//		if (id == null) {
//			return "redirect:/notice/list";
//		}
		
		// 뷰(jsp)에서 사용할 데이터를 model 객체에 저장
//		model.addAttribute("pageNum", pageNum);
		
		// 로그인 했을때는 글쓰기 화면으로 보여줌
		return "center/writeForm";
	} // Get - write
	
	
	@PostMapping("/write")
	public String write(String pageNum, NoticeVo noticeVo,
			HttpSession session, HttpServletRequest request) {
		// 로그인 안했을때는 글목록으로 리다이렉트 이동시킴
//		String id = (String) session.getAttribute("id");
//		if (id == null) {
//			return "redirect:/notice/list";
//		}
		
		// 클라이언트 IP주소, 등록날짜, 조회수 값 설정
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0); 
		
		int num = mySqlService.getNextNum("notice");
		noticeVo.setReRef(num);
		noticeVo.setReLev(0); 
		noticeVo.setReSeq(0);
		
		// 주글쓰기
		noticeService.addNotice(noticeVo);
		
		return "redirect:/notice/content?num=" + num + "&pageNum=" + pageNum;
	} // Post - write
	
	
	@GetMapping("/content")
	public String content(int num, String pageNum, Model model) {
		// 상세보기 대상 글의 조회수 1 증가
		noticeService.updateReadcount(num);
		
		// 상세보기 대상 글내용 VO로 가져오기
		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
		
		String content = "";
		if (noticeVo.getContent() != null) {
			content = noticeVo.getContent().replace("\n", "<br>");
			noticeVo.setContent(content);
		}
		
		model.addAttribute("noticeVo", noticeVo);
		model.addAttribute("pageNum", pageNum);
		
		return "center/content";
	} // content
	
	
	@GetMapping("/delete")
	public String delete(int num, String pageNum, RedirectAttributes rttr) {
		// 글번호에 해당하는 글 한개 삭제하기
		noticeService.deleteNoticeByNum(num);
		
		// 글목록 페이지로 리다이렉트 이동시키기
		rttr.addAttribute("pageNum", pageNum);
		
		return "redirect:/notice/list";
		//return "redirect:/notice/list?pageNum=" + pageNum;
	} // delete
	
	
	@GetMapping("/modify")
	public String modify(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// 글번호 num에 해당하는 글내용 VO로 가져오기
		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
		
		model.addAttribute("noticeVo", noticeVo);
		//model.addAttribute("pageNum", pageNum);
		
		return "center/modifyForm";
	} // GET - modify
	
	
	@PostMapping("/modify")
	public String modify(NoticeVo noticeVo, String pageNum, RedirectAttributes rttr) {
		
		noticeService.updateBoard(noticeVo);
		
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// 수정된 글의 상세보기 화면으로 리다이렉트 이동
		return "redirect:/notice/content";
	} // POST - modify
	
	
	@GetMapping("/replyWrite")
	public String replyWrite(
			@ModelAttribute("reRef") String reRef, 
			@ModelAttribute("reLev") String reLev, 
			@ModelAttribute("reSeq") String reSeq, 
			@ModelAttribute("pageNum") String pageNum, 
			Model model) {
		
//		model.addAttribute("reRef", reRef);
//		model.addAttribute("reLev", reLev);
//		model.addAttribute("reSeq", reSeq);
//		model.addAttribute("pageNum", pageNum);
		
		return "center/replyWriteForm";
	} // GET - replyWrite
	
	
	@PostMapping("/replyWrite")
	public String replyWrite(NoticeVo noticeVo, String pageNum, 
			HttpServletRequest request, RedirectAttributes rttr) {
		// reRef, reLev, reSeq 는 동일한 NoticeVo객체에 저장되지만
		// 답글 자체의 정보가 아니고 답글을 다는 대상글에 대한 정보임에 주의!!
		
		//insert될 글번호 가져오기
		int num = mySqlService.getNextNum("notice");
		noticeVo.setNum(num);
		
		//ip  regDate  readcount  값 저장
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0);  // 조회수
		
		// 답글 insert하기
		noticeService.updateAndAddReply(noticeVo);
		
		// 리다이렉트용 속성값을 설정
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// 글내용 상세보기 화면으로 리다이렉트 이동
		return "redirect:/notice/content";
	} // POST - replyWrite
}

