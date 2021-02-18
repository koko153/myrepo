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
	
	// ���� ��¥ ������ ���� ���ڿ� �������� 
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
	
	// �ֱ۾���
	@PostMapping("/write")
	public String write(HttpServletRequest request,
			@RequestParam(name = "filename", required = false) List<MultipartFile> multipartFiles,
			NoticeVo noticeVo, String pageNum) throws IOException {
		
		//============ �Խñ� NoticeVo �غ��ϱ� ==============
		// AUTO INCREMENT ������ȣ ��������
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
		//============ �Խñ� NoticeVo �غ�Ϸ� ==============
		
		
		
		//============ ���� ���ε带 ���� ���� �غ� ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp ������ �������
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile�� �̿��� ���Ͼ��ε� ���� ==============
		
		// AttachVo ÷���������� ���� ����Ʈ �غ�
		List<AttachVo> attachList = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// �����Է»��ڿ��� ������������ ��Ҵ� �ǳʶٱ�
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// ���� ���ε��� �����̸� ���ϱ�
				String filename = multipartFile.getOriginalFilename();
				
				// �ͽ��÷η��� �����̸��� ��ΰ� ���ԵǾ� �����Ƿ�
				// ���� �����̸��� �κй��ڿ��� ��������
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// ���ϸ� �ߺ��� ���ϱ� ���ؼ� �����̸� �տ� ���� UUID ���ڿ� ���ϱ�
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// ���ε�(����)�� �����̸�
				String uploadFilename = strUuid + "_" + filename;
				
				// ������ ���������� File ��ü�� �غ�
				File saveFile = new File(dir, uploadFilename);
				
				// �ӽþ��ε�� ������ ��������� ���ϸ����� ����(����)
				multipartFile.transferTo(saveFile);
				
				
				//============ ÷������ AttachVo �غ��ϱ� ==============
				AttachVo attachVo = new AttachVo();
				// �Խ��� �۹�ȣ ����
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// ������ ����� �̹��� ���� ��ο� �̸��� �غ�
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					
					// ����� �̹��� ���� �����ϱ�
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo �� DB�� insert�ϱ�
				//attachService.insertAttach(attachVo);
				
				attachList.add(attachVo);
			} // for
		} // if
		
		
		// NoticeVo �� DB�� insert�ϱ�
		//noticeService.addNotice(noticeVo);
		
		// NoticeVo�� AttachVo �������� Ʈ��������� insert�ϱ�
		noticeService.addNoticeAndAttaches(noticeVo, attachList);

		
		// �ڷ�� �Խ��� �󼼺���� �����̷�Ʈ
		return "redirect:/fileNotice/content?num=" + noticeVo.getNum() + "&pageNum=" + pageNum;
	} // POST - write
	
	
	@GetMapping("/content")
	public String content(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// ��ȸ�� 1 ����
		noticeService.updateReadcount(num);
		
		// ���1) ���ε��� select�ؼ� ��������
//		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
//		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		
		// ���2) ���� ������ �ѹ��� ��������
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
		// �Խñ۹�ȣ�� ÷�ε� ÷������ ����Ʈ ��������
		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		
		// application ��ü ���� ��������
		ServletContext application = request.getServletContext();
		// ���ε� ���ذ��
		String realPath = application.getRealPath("/"); // webapp
		
		// ÷������ �����ϱ�
		for (AttachVo attachVo : attachList) {
			String dir = realPath + "/upload/" + attachVo.getUploadpath();
			String filename = attachVo.getUuid() + "_" + attachVo.getFilename();
			// ������ ������ File Ÿ�� ��ü�� �غ�
			File file = new File(dir, filename);
			
			// ���� ���� Ȯ�� �� �����ϱ�
			if (file.exists()) {
				file.delete();
			}
			
			// �̹��� �����̸�
			if (isImage(attachVo.getFilename())) {
				// ������ �̹��� ���翩�� Ȯ�� �� �����ϱ�
				File thumbnailFile = new File(dir, "s_" + filename);
				if (thumbnailFile.exists()) {
					thumbnailFile.delete();
				}
			}
		} // for
		
		
		// attach ÷�����ϳ��� �����ϱ�
//		attachService.deleteAttachesByNoNum(num);
		// notice �Խñ� �����ϱ�
//		noticeService.deleteNoticeByNum(num);
		
		// notice �Խñ� �Ѱ��� attach ÷������ �������� Ʈ��������� �����ϱ�
		noticeService.deleteNoticeAndAttaches(num);
		
		// �۸������ �����̷�Ʈ �̵�
		return "redirect:/fileNotice/list?pageNum=" + pageNum;
	} // delete
	
	
	
	@GetMapping("/modify")
	public String modify(int num, @ModelAttribute("pageNum") String pageNum, Model model) {
		// �۹�ȣ num�� �ش��ϴ� �۳��� VO�� ��������
//		NoticeVo noticeVo = noticeService.getNoticeByNum(num);
//		List<AttachVo> attachList = attachService.getAttachesByNoNum(num);
		// �������� �ѹ��� ��������
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
		
		//============ ���� ���ε带 ���� ���� �غ� ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp ������ �������
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile�� �̿��� �ű����� ���ε� ���� ==============
		
		// AttachVo ÷���������� ���� ����Ʈ �غ�
		List<AttachVo> addAttaches = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// �����Է»��ڿ��� ������������ ��Ҵ� �ǳʶٱ�
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// ���� ���ε��� �����̸� ���ϱ�
				String filename = multipartFile.getOriginalFilename();
				
				// �ͽ��÷η��� �����̸��� ��ΰ� ���ԵǾ� �����Ƿ�
				// ���� �����̸��� �κй��ڿ��� ��������
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// ���ϸ� �ߺ��� ���ϱ� ���ؼ� �����̸� �տ� ���� UUID ���ڿ� ���ϱ�
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// ���ε�(����)�� �����̸�
				String uploadFilename = strUuid + "_" + filename;
				
				// ������ ���������� File ��ü�� �غ�
				File saveFile = new File(dir, uploadFilename);
				
				// �ӽþ��ε�� ������ ��������� ���ϸ����� ����(����)
				multipartFile.transferTo(saveFile);
				
				
				//============ ÷������ AttachVo �غ��ϱ� ==============
				AttachVo attachVo = new AttachVo();
				// �Խ��� �۹�ȣ ����
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// ������ ����� �̹��� ���� ��ο� �̸��� �غ�
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					// ����� �̹��� ���� �����ϱ�
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo �� DB�� insert�ϱ�
				//attachService.insertAttach(attachVo);

				// Ʈ����� ó���� ���� attachVo�� ����Ʈ�� �߰��ؼ� ������
				addAttaches.add(attachVo);
			} // for
		} // if
		
		
		//============ delFileNums �� ÷������ �����۾� ���� ==============
		
		if (delFileNums != null) {
			for (int num : delFileNums) {
				// ÷������ ��ȣ�� �ش��ϴ� ÷������ ���� �Ѱ��� VO�� ��������
				AttachVo attachVo = attachService.getAttachByNum(num);
				
				// ���������� �������� ���翩�� Ȯ���ؼ� �����ϱ�
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
				
				// ÷������ DB���̺� ÷�����Ϲ�ȣ�� �ش��ϴ� ���ڵ� �Ѱ� �����ϱ�
				//attachService.deleteAttachByNum(num);
			} // for
		} // if
		
		// ÷�����Ϲ�ȣ�鿡 �ش��ϴ� ÷������ ���ڵ�� �ϰ� �����ϱ�
		//attachService.deleteAttachesByNums(delFileNums);
		
		// �Խ��� ���̺� �� update�ϱ�
		//noticeService.updateBoard(noticeVo);
		
		// Ʈ����� ������ ���̺� ������ ó��
		noticeService.updateNoticeAndAddAttachesAndDeleteAttaches(noticeVo, addAttaches, delFileNums);
		
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// �󼼺��� ȭ������ �����̷�Ʈ �̵�
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
		// reRef, reLev, reSeq �� ������ NoticeVo��ü�� ���������
		// ��� ��ü�� ������ �ƴϰ� ����� �ٴ� ���ۿ� ���� �����ӿ� ����!!
		
		//insert�� �۹�ȣ ��������
		int num = mySqlService.getNextNum("notice");
		noticeVo.setNum(num);
		
		//ip  regDate  readcount  �� ����
		noticeVo.setIp(request.getRemoteAddr());
		noticeVo.setRegDate(new Timestamp(System.currentTimeMillis()));
		noticeVo.setReadcount(0);  // ��ȸ��
		
		
		//============ ���� ���ε带 ���� ���� �غ� ==============
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/");  // webapp ������ �������
		log.info("realPath : " + realPath);
		
		String strDate = this.getFolder();
		
		File dir = new File(realPath + "/upload", strDate);
		log.info("dir : " + dir.getPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		//============ MultipartFile�� �̿��� ���Ͼ��ε� ���� ==============
		
		// AttachVo ÷���������� ���� ����Ʈ �غ�
		List<AttachVo> attachList = new ArrayList<>();
		
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				// �����Է»��ڿ��� ������������ ��Ҵ� �ǳʶٱ�
				if (multipartFile.isEmpty()) {
					continue;
				}
				
				// ���� ���ε��� �����̸� ���ϱ�
				String filename = multipartFile.getOriginalFilename();
				
				// �ͽ��÷η��� �����̸��� ��ΰ� ���ԵǾ� �����Ƿ�
				// ���� �����̸��� �κй��ڿ��� ��������
				int beginIndex = filename.lastIndexOf("\\") + 1;
				filename = filename.substring(beginIndex);
				
				// ���ϸ� �ߺ��� ���ϱ� ���ؼ� �����̸� �տ� ���� UUID ���ڿ� ���ϱ�
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				
				// ���ε�(����)�� �����̸�
				String uploadFilename = strUuid + "_" + filename;
				
				// ������ ���������� File ��ü�� �غ�
				File saveFile = new File(dir, uploadFilename);
				
				// �ӽþ��ε�� ������ ��������� ���ϸ����� ����(����)
				multipartFile.transferTo(saveFile);
				
				
				//============ ÷������ AttachVo �غ��ϱ� ==============
				AttachVo attachVo = new AttachVo();
				// �Խ��� �۹�ȣ ����
				attachVo.setNoNum(noticeVo.getNum());
				
				attachVo.setUuid(strUuid);
				attachVo.setFilename(filename);
				attachVo.setUploadpath(strDate);
				
				if (isImage(filename)) {
					attachVo.setImage("I");
					
					// ������ ����� �̹��� ���� ��ο� �̸��� �غ�
					File thumbnailFile = new File(dir, "s_" + uploadFilename);
					// ����� �̹��� ���� �����ϱ�
					try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
						Thumbnailator.createThumbnail(multipartFile.getInputStream(), fos, 100, 100);
					}
				} else {
					attachVo.setImage("O");
				}
				
				// AttachVo �� DB�� insert�ϱ�
				//attachService.insertAttach(attachVo);
				
				attachList.add(attachVo);
			} // for
		} // if
		
		
		// ��� insert�ϱ�
//		noticeService.updateAndAddReply(noticeVo);
//		attachService.insertAttaches(attachList);
		
		// Ʈ����� ������ ó�� : ��� insert�� ÷������ insert
		noticeService.updateAndAddReplyAndAddAttaches(noticeVo, attachList);
		
		// �����̷�Ʈ�� �Ӽ����� ����
		rttr.addAttribute("num", noticeVo.getNum());
		rttr.addAttribute("pageNum", pageNum);
		
		// �۳��� �󼼺��� ȭ������ �����̷�Ʈ �̵�
		return "redirect:/fileNotice/content";
	} // POST - replyWrite
	
	
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> download(int num, HttpServletRequest request) throws Exception {
		// ÷������ ��ȣ�� �ش��ϴ� ���ڵ� �Ѱ� ��������
		AttachVo attachVo = attachService.getAttachByNum(num);
		
		ServletContext application = request.getServletContext();
		String realPath = application.getRealPath("/"); // webapp

		// �ٿ�ε��� ���������� File ��ü�� �غ�
		String dir = realPath + "/upload/" + attachVo.getUploadpath();
		String filename = attachVo.getUuid() + "_" + attachVo.getFilename();
		File file = new File(dir, filename);
		
		Resource resource = new FileSystemResource(file);
		
		if (!resource.exists()) {
			log.info("�ٿ�ε��� ������ �������� �ʽ��ϴ�.");
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND); // 404
		}
		
		String downloadFilename = attachVo.getFilename();
		System.out.println("utf-8 ���ϸ�: " + downloadFilename);

		// �ٿ�ε� ���ϸ��� ���ڼ��� utf-8���� iso-8859-1�� ��ȯ
		downloadFilename = new String(downloadFilename.getBytes("utf-8"), "iso-8859-1");
		System.out.println("iso-8859-1 ���ϸ�: " + downloadFilename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=" + downloadFilename);
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK); // 200�ڵ� ����
	} // download
	
	
}





