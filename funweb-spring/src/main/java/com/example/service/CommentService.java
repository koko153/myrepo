package com.example.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.CommentVo;
import com.example.domain.Criteria;
import com.example.mapper.CommentMapper;

import lombok.extern.java.Log;

@Service
@Log
public class CommentService {
	
	@Autowired
	private CommentMapper commentMapper;
	

	public int getTotalCountByNno(int nno) {
		int count = commentMapper.getTotalCountByNno(nno);
		return count;
	}
	
	// ¡÷¥Ò±€ æ≤±‚
	public int addComment(CommentVo commentVo) {
//		System.out.println(commentVo);
		log.info(commentVo.toString());
		
		int count = commentMapper.insert(commentVo);
		return count;
	}
	
	public CommentVo getCommentByCno(int cno) {
		CommentVo commentVo = commentMapper.getCommentByCno(cno);
		return commentVo;
	}
	
	public int deleteByCno(int cno) {
		int count = commentMapper.deleteByCno(cno);
		return count;
	}
	
	public void update(CommentVo commentVo) {
		commentMapper.update(commentVo);
	}
	
	public List<CommentVo> getComments(int nno) {
		List<CommentVo> list = commentMapper.getComments(nno);
		return list;
	}
	
	
	public List<CommentVo> getCommentsWithPaging(int nno, Criteria cri) {
		List<CommentVo> list = commentMapper.getCommentsWithPaging(nno, cri);
		return list;
	}
	
	// ¥‰¥Ò±€ æ≤±‚
	@Transactional
	public void addReplyComment(CommentVo commentVo) {
			
		commentMapper.updateReSeq(commentVo.getReRef(), commentVo.getReSeq());
		
		commentVo.setReLev(commentVo.getReLev() + 1);
		commentVo.setReSeq(commentVo.getReSeq() + 1);
		
		commentMapper.insert(commentVo);
	}
	
}
