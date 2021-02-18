package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.AttachVo;
import com.example.mapper.AttachMapper;

@Service
@Transactional
public class AttachService {

	@Autowired
	private AttachMapper attachMapper;
	
	public void insertAttach(AttachVo attachVo) {
		attachMapper.insertAttach(attachVo);
	}
	
	public void insertAttaches(List<AttachVo> attaches) {
		for (AttachVo attachVo : attaches) {
			attachMapper.insertAttach(attachVo);
		}
	}
	
	public AttachVo getAttachByNum(int num) {
		AttachVo attachVo = attachMapper.getAttachByNum(num);
		return attachVo;
	}
	
	public List<AttachVo> getAttachesByNoNum(int noNum) {
		List<AttachVo> attachList = attachMapper.getAttachesByNoNum(noNum);
		return attachList;
	}
	
	public void deleteAttachByNum(int num) {
		attachMapper.deleteAttachByNum(num);
	}
	
	public void deleteAttachesByNoNum(int noNum) {
		attachMapper.deleteAttachesByNoNum(noNum);
	}
	
	public void deleteAttachesByNums(List<Integer> numList) {
		attachMapper.deleteAttachesByNums(numList);
	}
	
}
