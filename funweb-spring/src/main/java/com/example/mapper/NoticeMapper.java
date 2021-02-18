package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.domain.NoticeVo;

public interface NoticeMapper {

//	@Insert("INSERT INTO notice (id, subject, content, readcount, reg_date, ip, re_ref, re_lev, re_seq) "
//			+ "VALUES (#{id}, #{subject}, #{content}, #{readcount}, #{regDate}, #{ip}, #{reRef}, #{reLev}, #{reSeq})")
	void addNotice(NoticeVo noticeVo);
	
	
	@Select("SELECT * FROM notice WHERE num = #{num}")
	NoticeVo getNoticeByNum(int num);
	
	
	
	void updateReadcount(int num);
	
	
//	@Select("SELECT COUNT(*) FROM notice")
	int getCountAll();
	
	
	List<NoticeVo> getNotices(@Param("startRow") int startRow, @Param("pageSize") int pageSize);
	
	
	void updateBoard(NoticeVo noticeVo);
	
	@Delete("DELETE FROM notice WHERE num = #{num}")
	void deleteNoticeByNum(int num);
	
	@Delete("DELETE FROM notice")
	void deleteAll();
	
	
	void updateReSeq(
			@Param("reRef") int reRef, 
			@Param("reSeq") int reSeq);
	
	
	
	int getCountBySearch(
			@Param("category") String category, 
			@Param("search") String search);
	
	
	List<NoticeVo> getNoticesBySearch(
			@Param("startRow") int startRow, 
			@Param("pageSize") int pageSize, 
			@Param("category") String category, 
			@Param("search") String search);
	
	
	NoticeVo getNoticeAndAttaches(int num);
	
	
	
	List<NoticeVo> getNoticesByNums(@Param("numList") List<Integer> numList);
	
	
}






