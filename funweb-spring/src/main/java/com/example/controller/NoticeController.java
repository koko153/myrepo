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
		// �α��� ���������� �۸������ �����̷�Ʈ �̵���Ŵ
//		String id = (String) session.getAttribute("id");
//		if (id == null) {
//			return "redirect:/notice/list";
//		}
		
		// ��(jsp)���� ����� �����͸� model ��ü�� ����
//		model.addAttribute("pageNum", pageNum);
		
		// �α��� �������� �۾��� ȭ������ ������
		return "center/writeForm";
	} // Get - write
	
	
	@PostMapping("/write")
	public String write(String pageNum, NoticeVo noticeVo,
			HttpSession session, HttpServletRequest request) {
		// �α��� ���������� �۸������ �����̷�Ʈ �̵���Ŵ
//		String id = (String) session.getAttribute("id");
//		if (id == null) {
//			return "redirect:/notice/list";
//		}
		
		// Ŭ���̾�Ʈ IP�ּ�, ��ϳ�¥, ��ȸ�� �� ����
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0); 
		
		int num = mySqlService.getNextNum("notice");
		noticeVo.setReRef(num);
		noticeVo.setReLev(0); 
		noticeVo.setReSeq(0);
		
		// �ֱ۾���
		noticeService.addNotice(noticeVo);
		
		return "redirect:/notice/content?num=" + num + "&pageNum=" + pageNum;
	} // Post - write
	
	
	@GetMapping("/content")
	public String content(int num, String pageNum, Model model) {
		// �󼼺��� ��� ���� ��ȸ�� 1 ����
		noticeService.updateReadcount(num);
		
		// �󼼺��� ��� �۳��� VO�� ��������
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
		// �۹�ȣ�� �ش��ϴ� �� �Ѱ� �����ϱ�
		noticeService.deleteNoticeByNum(num);
		
		// �۸�� �������� �����̷�Ʈ �̵���Ű��
		rttr.addAttribute("pageNum", pageNum);
		
		return "redirect:/notice/list";
		//return "redirect:/notice/list?pageNum=" + pageNum;
	} // delete
	
	
	@GetMapping("/modify")
	public String modify(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// �۹�ȣ num�� �ش��ϴ� �۳��� VO�� ��������
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
		
		// ������ ���� �󼼺��� ȭ������ �����̷�Ʈ �̵�
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
		// reRef, reLev, reSeq �� ������ NoticeVo��ü�� ���������
		// ��� ��ü�� ������ �ƴϰ� ����� �ٴ� ���ۿ� ���� �����ӿ� ����!!
		
		//insert�� �۹�ȣ ��������
		int num = mySqlService.getNextNum("notice");
		noticeVo.setNum(num);
		
		//ip  regDate  readcount  �� ����
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0);  // ��ȸ��
		
		// ��� insert�ϱ�
		noticeService.updateAndAddReply(noticeVo);
		
		// �����̷�Ʈ�� �Ӽ����� ����
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// �۳��� �󼼺��� ȭ������ �����̷�Ʈ �̵�
		return "redirect:/notice/content";
	} // POST - replyWrite
}

