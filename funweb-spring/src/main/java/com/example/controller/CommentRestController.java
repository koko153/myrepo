package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.CommentVo;
import com.example.domain.Criteria;
import com.example.service.CommentService;
import com.example.service.MySqlService;

import lombok.extern.java.Log;

/*
  REST ��Ʈ�ѷ��� HTTP method ���� ���
   POST   - Create (SQL Insert��)
   GET    - Read   (SQL Select��)
   PUT    - Update (SQL Update��)
   DELETE - Delete (SQL Delete��)
 */
@RestController // �� ��Ʈ�ѷ��� ��� �޼ҵ��� ���ϰ��� JSON �Ǵ� XML �������� ������
@RequestMapping("/comment/*")
@Log
public class CommentRestController {

	@Autowired
	private CommentService commentService;
	@Autowired
	private MySqlService mySqlService;
	
	
	@GetMapping("/one/{cno}")
	public ResponseEntity<CommentVo> getOne(@PathVariable("cno") int cno) {
		
		CommentVo commentVo = commentService.getCommentByCno(cno);
		
		ResponseEntity<CommentVo> entity = new ResponseEntity<CommentVo>(commentVo, HttpStatus.OK);
		return entity;
	} // getComment
	
	@GetMapping("/pages/{nno}")
	public ResponseEntity<List<CommentVo>> getList(@PathVariable("nno") int nno) {
		List<CommentVo> commentList = commentService.getComments(nno);
		return new ResponseEntity<List<CommentVo>>(commentList, HttpStatus.OK);
	}
	
	@GetMapping("/pages/{nno}/{pageNum}/{numOfRows}")
	public ResponseEntity<Map<String, Object>> getListWithPage(@PathVariable("nno") int nno,
			@PathVariable("pageNum") int pageNum,
			@PathVariable("numOfRows") int numOfRows) {
		
		Criteria cri = new Criteria(pageNum, numOfRows);
		
		List<CommentVo> commentList = commentService.getCommentsWithPaging(nno, cri);
		
		int totalCount = commentService.getTotalCountByNno(nno);
		
		Map<String, Object> map = new HashMap<>();
		map.put("commentList", commentList);
		map.put("totalCount", totalCount);
		
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	
	
	// �ִ�� ����
	@PostMapping(value = "/new", consumes = "application/json", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<CommentVo> createMain(@RequestBody CommentVo commentVo, 
			HttpSession session) {
		String id = (String) session.getAttribute("id");
		
		int cno = mySqlService.getNextNum("comment");
		commentVo.setCno(cno);
		commentVo.setReRef(cno);
		commentVo.setReLev(0);
		commentVo.setReSeq(0);
		commentVo.setId(id);
		log.info("commentVo : " + commentVo);
		
		commentService.addComment(commentVo);
		
		commentVo = commentService.getCommentByCno(cno);
		
		return new ResponseEntity<CommentVo>(commentVo, HttpStatus.OK);
	} // createMain
	
	
	// ���� ����
	@PostMapping(value = "/new/reply")
	public ResponseEntity<CommentVo> createReply(@RequestBody CommentVo commentVo, 
			HttpSession session) {
		String id = (String) session.getAttribute("id");
		
		int cno = mySqlService.getNextNum("comment");
		commentVo.setCno(cno);
		commentVo.setId(id);
		log.info("���� : " + commentVo.toString());
		
		commentService.addReplyComment(commentVo);
		
		commentVo = commentService.getCommentByCno(cno);
		
		return new ResponseEntity<CommentVo>(commentVo, HttpStatus.OK);
	} // createReply
	
	
	@DeleteMapping(value = "/{cno}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> remove(@PathVariable("cno") int cno) {
		int count = commentService.deleteByCno(cno);
		
		return (count > 0) 
				? new ResponseEntity<String>("success", HttpStatus.OK)
					: new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	} // remove
	
	
	@PutMapping("/modify")
	public ResponseEntity<CommentVo> modify(@RequestBody CommentVo commentVo) {
		log.info("commentVo : " + commentVo);
		
		commentService.update(commentVo);
		
		CommentVo commentVoGet = commentService.getCommentByCno(commentVo.getCno());
		
		return new ResponseEntity<CommentVo>(commentVoGet, HttpStatus.OK);
	} // modify
	
}





