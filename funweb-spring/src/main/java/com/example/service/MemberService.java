package com.example.service;

import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.MemberVo;
import com.example.mapper.MemberMapper;

import lombok.extern.java.Log;

@Log
@Service
@Transactional  // 이 클래스의 모든 메소드가 각각 한개의 트랜잭션 단위로 수행됨
public class MemberService {

	// 스프링 빈으로 등록된 객체들 중에서
	// 타입으로 객체의 참조를 가져와서 참조변수에 저장해줌
	private MemberMapper memberMapper;
	
	@Autowired
	public void setMemberMapper(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}

	public MemberVo getMemberById(String id) {
		MemberVo memberVo = memberMapper.getMemberById(id);
		return memberVo;
	}
	
	public void addMember(MemberVo memberVo) {
		memberMapper.addMember(memberVo);
	}
	
	public List<MemberVo> getAllMembers() {
		List<MemberVo> list = memberMapper.getAllMembers();
		return list;
	}
	
	
	public int userCheck(String id, String passwd) {
		int check = -1;
		
		String dbPasswd = memberMapper.userCheck(id);
		
		if (dbPasswd != null) {
			if (BCrypt.checkpw(passwd, dbPasswd)) { // passwd.equals(dbPasswd)
				check = 1;
			} else {
				check = 0;
			}
		} else { // dbPasswd == null
			check = -1;
		}
		return check;
	}
	
	public int getCountById(String id) {
		int count = memberMapper.getCountById(id);
		return count;
	}
	
	public void update(MemberVo memberVo) {
		memberMapper.update(memberVo);
	}
	
	public void deleteById(String id) {
		memberMapper.deleteById(id);
	}
	
	public void deleteAll() {
		memberMapper.deleteAll();
	}
	
	public List<Map<String, Object>> getGenderPerCount() {
		List<Map<String, Object>> list = memberMapper.getGenderPerCount();
		return list;
	}
	
	public List<Map<String, Object>> getAgeRangePerCount() {
		List<Map<String, Object>> list = memberMapper.getAgeRangePerCount();
		return list;
	}
	
}







