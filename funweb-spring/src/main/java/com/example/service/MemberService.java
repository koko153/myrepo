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
@Transactional  // �� Ŭ������ ��� �޼ҵ尡 ���� �Ѱ��� Ʈ����� ������ �����
public class MemberService {

	// ������ ������ ��ϵ� ��ü�� �߿���
	// Ÿ������ ��ü�� ������ �����ͼ� ���������� ��������
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







