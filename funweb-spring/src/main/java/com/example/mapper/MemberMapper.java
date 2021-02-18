package com.example.mapper;

import java.util.List;
import java.util.Map;

import com.example.domain.MemberVo;

// ���̹�Ƽ������ Mapper �������̽��� ������ ������ ��!
public interface MemberMapper {

	// ������ �޼ҵ尡 �Ű������� 2�� �̻� ��������
	// @Param �ֳ����̼� ������ �̸��� �����ؾ� ��
	
	//@Select("SELECT * FROM member WHERE id = #{id}")
	MemberVo getMemberById(String id);
	
	//@Select("SELECT * FROM member ORDER BY id")
	List<MemberVo> getAllMembers();
	
	
	void addMember(MemberVo memberVo);
	
	//@Select("SELECT passwd FROM member WHERE id = #{id}")
	String userCheck(String id);
	
	//@Select("SELECT COUNT(*) FROM member WHERE id = #{id}")
	int getCountById(String id);
	
	
	void update(MemberVo memberVo);
	
	//@Delete("DELETE FROM member WHERE id = #{id} ")
	void deleteById(String id);
	
	//@Delete("DELETE FROM member")
	void deleteAll();
	
	
//	@Select("SELECT gender, count(*) as cnt "
//			+ "FROM member "
//			+ "GROUP BY gender ")
	List<Map<String, Object>> getGenderPerCount();
	
	
	
	List<Map<String, Object>> getAgeRangePerCount();
	
	
}






