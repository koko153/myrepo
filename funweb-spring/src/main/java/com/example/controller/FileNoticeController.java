package com.example.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.domain.AttachVo;
import com.example.domain.NoticeVo;
import com.example.domain.PageDto;
import com.example.service.AttachService;
import com.example.service.MySqlService;
import com.example.service.NoticeService;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;


@Controller
@RequestMapping("/fileNotice/*")
@Slf4j
public class FileNoticeController {
	
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private AttachService attachService;
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
		
		return "center/fileNotice";
	} // list
	
	
	@GetMapping("/write")
	public String write(@ModelAttribute("pageNum") String pageNum, Model model) {
		
//		model.addAttribute("pageNum", pageNum);
		
		return "center/fileWriteForm";
	} // GET - write
	
	// 오늘 날짜 형식의 폴더 문자열 가져오기 
	private String getFolder() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String strDate = sdf.format(date); // "2020/11/11"
		return strDate;
	}
	
	private boolean isImage(String filename) {
		boolean result = false;
		
		// aaaa.bbb.ccc.ddd
		int index = filename.lastIndexOf(".");
		String ext = filename.substring(index + 1);
		
		if (ext.equalsIgnoreCase("jpg") 
				|| ext.equalsIgnoreCase("jpeg")
				|| ext.equalsIgnoreCase("gif")
				|| ext.equalsIgnoreCase("png")) {
			result = true;
		}
		return result;
	}
	
	// 주글쓰기
	@PostMapping("/write")
	public String write(HttpServletRequest request,
			@RequestParam(name = "filename", required = false) List<MultipartFile> multipartFiles,
			NoticeVo noticeVo, String pageNum) throws IOException {
		
		//============ 게시글 NoticeVo 준비하기 ==============
		// AUTO INCREMENT 다음번호 가져오기
		int num = mySqlService.getNextNum("notice");
		noticeVo.setNum(num);

		//ip  regDate  readcount  
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0);

		//re_ref  re_lev  re_seq
		noticeVo.setReRef(num);
		noticeVo.setReLev(0);
		noticeVo.setReSeq(0);
		//============ 게시글 NoticeVo 준비완료 ==============
		
		
		
		//============ 파일 업로드를 위한 폴더 준비 ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp 폴더의 실제경로
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile을 이용해 파일업로드 수행 ==============
		
		// AttachVo 첨부파일정보 담을 리스트 준비
		List<AttachVo> attachList = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// 파일입력상자에서 선택하지않은 요소는 건너뛰기
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// 실제 업로드한 파일이름 구하기
				String filename = multipartFile.getOriginalFilename();
				
				// 익스플로러는 파일이름에 경로가 포함되어 있으므로
				// 순수 파일이름만 부분문자열로 가져오기
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// 파일명 중복을 피하기 위해서 파일이름 앞에 붙일 UUID 문자열 구하기
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// 업로드(생성)할 파일이름
				String uploadFilename = strUuid + "_" + filename;
				
				// 생성할 파일정보를 File 객체로 준비
				File saveFile = new File(dir, uploadFilename);
				
				// 임시업로드된 파일을 지정경로의 파일명으로 생성(복사)
				multipartFile.transferTo(saveFile);
				
				
				//============ 첨부파일 AttachVo 준비하기 ==============
				AttachVo attachVo = new AttachVo();
				// 게시판 글번호 설정
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// 생성할 썸네일 이미지 파일 경로와 이름을 준비
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					
					// 썸네일 이미지 파일 생성하기
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo 를 DB에 insert하기
				//attachService.insertAttach(attachVo);
				
				attachList.add(attachVo);
			} // for
		} // if
		
		
		// NoticeVo 를 DB에 insert하기
		//noticeService.addNotice(noticeVo);
		
		// NoticeVo와 AttachVo 여러개를 트랜잭션으로 insert하기
		noticeService.addNoticeAndAttaches(noticeVo, attachList);

		
		// 자료실 게시판 상세보기로 리다이렉트
		return "redirect:/fileNotice/content?num=" + noticeVo.getNum() + "&pageNum=" + pageNum;
	} // POST - write
	
	
	@GetMapping("/content")
	public String content(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// 조회수 1 증가
		noticeService.updateReadcount(num);
		
		// 방법1) 따로따로 select해서 가져오기
//		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
//		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		
		// 방법2) 조인 쿼리로 한번에 가져오기
		NoticeVo noticeVo = noticeService.getNoticeAndAttaches(num);
		
		String content = "";
		if (noticeVo.getContent() != null) {
			content = noticeVo.getContent().replace("\n", "<br>");
			noticeVo.setContent(content);
		}
		
		
		model.addAttribute("noticeVo", noticeVo);
		model.addAttribute("attachList", noticeVo.getAttachList());
		
		return "center/fileContent";
	} // content
	
	
	@GetMapping("delete")
	public String delete(int num, String pageNum, HttpServletRequest request) {
		// 게시글번호에 첨부된 첨부파일 리스트 가져오기
		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		
		// application 객체 참조 가져오기
		ServletContext application = request.getServletContext();
		// 업로드 기준경로
		String realPath = application.getRealPath("/"); // webapp
		
		// 첨부파일 삭제하기
		for (AttachVo attachVo : attachList) {
			String dir = realPath + "/upload/" + attachVo.getUploadpath();
			String filename = attachVo.getUuid() + "_" + attachVo.getFilename();
			// 삭제할 파일을 File 타입 객체로 준비
			File file = new File(dir, filename);
			
			// 파일 존재 확인 후 삭제하기
			if (file.exists()) {
				file.delete();
			}
			
			// 이미지 파일이면
			if (isImage(attachVo.getFilename())) {
				// 섬네일 이미지 존재여부 확인 후 삭제하기
				File thumbnailFile = new File(dir, "s_" + filename);
				if (thumbnailFile.exists()) {
					thumbnailFile.delete();
				}
			}
		} // for
		
		
		// attach 첨부파일내용 삭제하기
//		attachService.deleteAttachesByNoNum(num);
		// notice 게시글 삭제하기
//		noticeService.deleteNoticeByNum(num);
		
		// notice 게시글 한개와 attach 첨부파일 여러개를 트랜잭션으로 삭제하기
		noticeService.deleteNoticeAndAttaches(num);
		
		// 글목록으로 리다이렉트 이동
		return "redirect:/fileNotice/list?pageNum=" + pageNum;
	} // delete
	
	
	
	@GetMapping("/modify")
	public String modify(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// 글번호 num에 해당하는 글내용 VO로 가져오기
//		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
//		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		// 조인으로 한번에 가져오기
		NoticeVo noticeVo = noticeService.getNoticeAndAttaches(num);
		List<AttachVo> attachList = noticeVo.getAttachList();
		int fileCount = attachList.size();
		
		model.addAttribute("noticeVo", noticeVo);
		model.addAttribute("attachList", attachList);
		model.addAttribute("fileCount", fileCount);
		
		return "center/fileModifyForm";
	} // GET - modify
	
	
	@PostMapping("/modify")
	public String modify(HttpServletRequest request,
			@RequestParam(name = "filename", required = false) List<MultipartFile> multipartFiles,
			NoticeVo noticeVo, String pageNum,
			@RequestParam(name = "delfile", required = false) List<Integer> delFileNums,
			RedirectAttributes rttr) throws IOException {
		
		//============ 파일 업로드를 위한 폴더 준비 ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp 폴더의 실제경로
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile을 이용해 신규파일 업로드 수행 ==============
		
		// AttachVo 첨부파일정보 담을 리스트 준비
		List<AttachVo> addAttaches = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// 파일입력상자에서 선택하지않은 요소는 건너뛰기
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// 실제 업로드한 파일이름 구하기
				String filename = multipartFile.getOriginalFilename();
				
				// 익스플로러는 파일이름에 경로가 포함되어 있으므로
				// 순수 파일이름만 부분문자열로 가져오기
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// 파일명 중복을 피하기 위해서 파일이름 앞에 붙일 UUID 문자열 구하기
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// 업로드(생성)할 파일이름
				String uploadFilename = strUuid + "_" + filename;
				
				// 생성할 파일정보를 File 객체로 준비
				File saveFile = new File(dir, uploadFilename);
				
				// 임시업로드된 파일을 지정경로의 파일명으로 생성(복사)
				multipartFile.transferTo(saveFile);
				
				
				//============ 첨부파일 AttachVo 준비하기 ==============
				AttachVo attachVo = new AttachVo();
				// 게시판 글번호 설정
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// 생성할 썸네일 이미지 파일 경로와 이름을 준비
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					// 썸네일 이미지 파일 생성하기
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo 를 DB에 insert하기
				//attachService.insertAttach(attachVo);

				// 트랜잭션 처리를 위해 attachVo를 리스트에 추가해서 모으기
				addAttaches.add(attachVo);
			} // for
		} // if
		
		
		//============ delFileNums 로 첨부파일 삭제작업 수행 ==============
		
		if (delFileNums != null) {
			for (int num : delFileNums) {
				// 첨부파일 번호에 해당하는 첨부파일 정보 한개를 VO로 가져오기
				AttachVo attachVo = attachService.getAttachByNum(num);
				
				// 파일정보로 실제파일 존재여부 확인해서 삭제하기
				String path = realPath + "/upload/" + attachVo.getUploadpath();
				String file = attachVo.getUuid() + "_" + attachVo.getFilename();
				
				File delFile = new File(path, file);
				if (delFile.exists()) {
					delFile.delete();
				}
				
				if (isImage(attachVo.getFilename())) {
					File thumbnailFile = new File(path, "s_" + file);
					if (thumbnailFile.exists()) {
						thumbnailFile.delete();
					}
				}
				
				// 첨부파일 DB테이블에 첨부파일번호에 해당하는 레코드 한개 삭제하기
				//attachService.deleteAttachByNum(num);
			} // for
		} // if
		
		// 첨부파일번호들에 해당하는 첨부파일 레코드들 일괄 삭제하기
		//attachService.deleteAttachesByNums(delFileNums);
		
		// 게시판 테이블 글 update하기
		//noticeService.updateBoard(noticeVo);
		
		// 트랜잭션 단위로 테이블 데이터 처리
		noticeService.updateNoticeAndAddAttachesAndDeleteAttaches(noticeVo, addAttaches, delFileNums);
		
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// 상세보기 화면으로 리다이렉트 이동
		return "redirect:/fileNotice/content";
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
		
		return "center/fileReplyWriteForm";
	} // GET - replyWrite
	
	
	@PostMapping("/replyWrite")
	public String replyWrite(NoticeVo noticeVo, String pageNum, 
			@RequestParam(name = "filename", required = false) List<MultipartFile> multipartFiles,
			HttpServletRequest request, RedirectAttributes rttr) throws IOException {
		// reRef, reLev, reSeq 는 동일한 NoticeVo객체에 저장되지만
		// 답글 자체의 정보가 아니고 답글을 다는 대상글에 대한 정보임에 주의!!
		
		//insert될 글번호 가져오기
		int num = mySqlService.getNextNum("notice");
		noticeVo.setNum(num);
		
		//ip  regDate  readcount  값 저장
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0);  // 조회수
		
		
		//============ 파일 업로드를 위한 폴더 준비 ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp 폴더의 실제경로
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile을 이용해 파일업로드 수행 ==============
		
		// AttachVo 첨부파일정보 담을 리스트 준비
		List<AttachVo> attachList = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// 파일입력상자에서 선택하지않은 요소는 건너뛰기
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// 실제 업로드한 파일이름 구하기
				String filename = multipartFile.getOriginalFilename();
				
				// 익스플로러는 파일이름에 경로가 포함되어 있으므로
				// 순수 파일이름만 부분문자열로 가져오기
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// 파일명 중복을 피하기 위해서 파일이름 앞에 붙일 UUID 문자열 구하기
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// 업로드(생성)할 파일이름
				String uploadFilename = strUuid + "_" + filename;
				
				// 생성할 파일정보를 File 객체로 준비
				File saveFile = new File(dir, uploadFilename);
				
				// 임시업로드된 파일을 지정경로의 파일명으로 생성(복사)
				multipartFile.transferTo(saveFile);
				
				
				//============ 첨부파일 AttachVo 준비하기 ==============
				AttachVo attachVo = new AttachVo();
				// 게시판 글번호 설정
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// 생성할 썸네일 이미지 파일 경로와 이름을 준비
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					// 썸네일 이미지 파일 생성하기
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo 를 DB에 insert하기
				//attachService.insertAttach(attachVo);
				
				attachList.add(attachVo);
			} // for
		} // if
		
		
		// 답글 insert하기
//		noticeService.updateAndAddReply(noticeVo);
//		attachService.insertAttaches(attachList);
		
		// 트랜잭션 단위로 처리 : 답글 insert와 첨부파일 insert
		noticeService.updateAndAddReplyAndAddAttaches(noticeVo, attachList);
		
		// 리다이렉트용 속성값을 설정
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// 글내용 상세보기 화면으로 리다이렉트 이동
		return "redirect:/fileNotice/content";
	} // POST - replyWrite
	
	
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> download(int num, HttpServletRequest request) throws Exception {
		// 첨부파일 번호에 해당하는 레코드 한개 가져오기
		AttachVo attachVo = attachService.getAttachByNum(num);
		
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/"); // webapp

		// 다운로드할 파일정보를 File 객체로 준비
		String dir = realPath + "/upload/" + attachVo.getUploadpath();
		String filename = attachVo.getUuid() + "_" + attachVo.getFilename();
		File file = new File(dir, filename);
		
		Resource resource = new FileSystemResource(file);
		
		if (!resource.exists()) {
			log.info("다운로드할 파일이 존재하지 않습니다.");
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND); // 404
		}
		
		String downloadFilename = attachVo.getFilename();
		System.out.println("utf-8 파일명: " + downloadFilename);

		// 다운로드 파일명의 문자셋을 utf-8에서 iso-8859-1로 변환
		downloadFilename = new String(downloadFilename.getBytes("utf-8"), "iso-8859-1");
		System.out.println("iso-8859-1 파일명: " + downloadFilename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=" + downloadFilename);
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK); // 200코드 정상
	} // download
	
	
}





