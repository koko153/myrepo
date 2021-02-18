package com.example.service;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.AttachVo;
import com.example.domain.NoticeVo;
import com.example.mapper.AttachMapper;
import com.example.mapper.NoticeMapper;

@Service
public class NoticeService {
	
	@Autowired
	private NoticeMapper noticeMapper;
	@Autowired
	private AttachMapper attachMapper;
	
	
	// 주글쓰기
	public void addNotice(NoticeVo noticeVo) {
		noticeMapper.addNotice(noticeVo);
	}
	
	
	public NoticeVo getNoticeByNum(int num) {
		NoticeVo noticeVo = noticeMapper.getNoticeByNum(num);
		return noticeVo;
	}
	
	
	
	public void updateReadcount(int num) {
		noticeMapper.updateReadcount(num);
	}
	
	
	
	public int getCountAll() {
		int count = noticeMapper.getCountAll();
		return count;
	}
	
	
	public List<NoticeVo> getNotices(int startRow, int pageSize) {
		List<NoticeVo> list = noticeMapper.getNotices(startRow, pageSize);
		return list;
	}
	
	
	public void updateBoard(NoticeVo noticeVo) {
		noticeMapper.updateBoard(noticeVo);
	}
	
	public void deleteNoticeByNum(int num) {
		noticeMapper.deleteNoticeByNum(num);
	}
	
	@Transactional
	public void deleteNoticeAndAttaches(int num) {
		noticeMapper.deleteNoticeByNum(num);
		attachMapper.deleteAttachesByNoNum(num);
	}
	
	public void deleteAll() {
		noticeMapper.deleteAll();
	}
	
	// 답글쓰기
	@Transactional
	public void updateAndAddReply(NoticeVo noticeVo) {
		// 답글을 쓰는 대상글과 같은 글그룹에서 
		// 답글을 쓰는 대상글의 순번보다 큰 글의 순번을 1씩 증가시킴
		noticeMapper.updateReSeq(noticeVo.getReRef(), noticeVo.getReSeq());
		
		// insert할 답글정보로 수정
		noticeVo.setReLev(noticeVo.getReLev() + 1);
		noticeVo.setReSeq(noticeVo.getReSeq() + 1);
		
		// 답글 insert하기
		noticeMapper.addNotice(noticeVo);
	}
	
	
	public int getCountBySearch(String category, String search) {
		int count = noticeMapper.getCountBySearch(category, search);
		return count;
	}
	
	
	public List<NoticeVo> getNoticesBySearch(int startRow, int pageSize, String category, String search) {
		return noticeMapper.getNoticesBySearch(startRow, pageSize, category, search);
	}
	
	
	public NoticeVo getNoticeAndAttaches(int num) {
		return noticeMapper.getNoticeAndAttaches(num);
	}
	
	//public List<NoticeVo> getNoticesByNums(List<Integer> numList)
	public List<NoticeVo> getNoticesByNums(Integer... numArr) {
		
		List<Integer> numList = Arrays.asList(numArr);
		
		return noticeMapper.getNoticesByNums(numList);
	}
	
	
	// 자료실 게시판 주글쓰기
	@Transactional
	public void addNoticeAndAttaches(NoticeVo noticeVo, List<AttachVo> attachList) {
		// 게시글 등록
		noticeMapper.addNotice(noticeVo);
		
		// 첨부파일정보 등록
		for (AttachVo attachVo : attachList) {
			attachMapper.insertAttach(attachVo);
		}
	}
	
	// 자료실 게시판 답글쓰기
	@Transactional
	public void updateAndAddReplyAndAddAttaches(NoticeVo noticeVo, List<AttachVo> attachList) {
		// 답글을 쓰는 대상글과 같은 글그룹에서 
		// 답글을 쓰는 대상글의 순번보다 큰 글의 순번을 1씩 증가시킴
		noticeMapper.updateReSeq(noticeVo.getReRef(), noticeVo.getReSeq());
		
		// insert할 답글정보로 수정
		noticeVo.setReLev(noticeVo.getReLev() + 1);
		noticeVo.setReSeq(noticeVo.getReSeq() + 1);
		
		// 답글 insert하기
		noticeMapper.addNotice(noticeVo);
		
		
		// 첨부파일 정보 insert
		for (AttachVo attachVo : attachList) {
			attachMapper.insertAttach(attachVo);
		}
	}
	
	
	@Transactional
	public void updateNoticeAndAddAttachesAndDeleteAttaches(NoticeVo noticeVo, List<AttachVo> attaches, List<Integer> delFileNums) {
		noticeMapper.updateBoard(noticeVo);
		
		for (AttachVo attachVo : attaches) {
			attachMapper.insertAttach(attachVo);
		}
		
		if (delFileNums != null) {
			attachMapper.deleteAttachesByNums(delFileNums);
		}
	}
	
}





