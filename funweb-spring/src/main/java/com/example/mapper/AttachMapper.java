package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.domain.AttachVo;

public interface AttachMapper {
	
	int insertAttach(AttachVo attachVo);
	
	@Select("SELECT * FROM attach WHERE num = #{num}")
	AttachVo getAttachByNum(int num);
	
	@Select("SELECT * FROM attach WHERE no_num = #{noNum}")
	List<AttachVo> getAttachesByNoNum(int noNum);
	
	@Delete("DELETE FROM attach where num = #{num}")
	int deleteAttachByNum(int num);
	
	@Delete("DELETE FROM attach where no_num = #{noNum}")
	int deleteAttachesByNoNum(int noNum);
	
	
	void deleteAttachesByNums(@Param("numList") List<Integer> numList);
}




