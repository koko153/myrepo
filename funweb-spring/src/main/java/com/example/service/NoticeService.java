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
	
	
	// �ֱ۾���
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
	
	// ��۾���
	@Transactional
	public void updateAndAddReply(NoticeVo noticeVo) {
		// ����� ���� ���۰� ���� �۱׷쿡�� 
		// ����� ���� ������ �������� ū ���� ������ 1�� ������Ŵ
		noticeMapper.updateReSeq(noticeVo.getReRef(), noticeVo.getReSeq());
		
		// insert�� ��������� ����
		noticeVo.setReLev(noticeVo.getReLev() + 1);
		noticeVo.setReSeq(noticeVo.getReSeq() + 1);
		
		// ��� insert�ϱ�
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
	
	
	// �ڷ�� �Խ��� �ֱ۾���
	@Transactional
	public void addNoticeAndAttaches(NoticeVo noticeVo, List<AttachVo> attachList) {
		// �Խñ� ���
		noticeMapper.addNotice(noticeVo);
		
		// ÷���������� ���
		for (AttachVo attachVo : attachList) {
			attachMapper.insertAttach(attachVo);
		}
	}
	
	// �ڷ�� �Խ��� ��۾���
	@Transactional
	public void updateAndAddReplyAndAddAttaches(NoticeVo noticeVo, List<AttachVo> attachList) {
		// ����� ���� ���۰� ���� �۱׷쿡�� 
		// ����� ���� ������ �������� ū ���� ������ 1�� ������Ŵ
		noticeMapper.updateReSeq(noticeVo.getReRef(), noticeVo.getReSeq());
		
		// insert�� ��������� ����
		noticeVo.setReLev(noticeVo.getReLev() + 1);
		noticeVo.setReSeq(noticeVo.getReSeq() + 1);
		
		// ��� insert�ϱ�
		noticeMapper.addNotice(noticeVo);
		
		
		// ÷������ ���� insert
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





