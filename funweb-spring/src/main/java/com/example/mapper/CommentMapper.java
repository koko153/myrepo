package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.domain.CommentVo;
import com.example.domain.Criteria;

public interface CommentMapper {

	@Select("SELECT COUNT(*) FROM comment WHERE nno = #{nno}")
	int getTotalCountByNno(int nno);
	
	@Insert("INSERT INTO comment (nno, id, content, re_ref, re_lev, re_seq) "
			+ "VALUES (#{nno}, #{id}, #{content}, #{reRef}, #{reLev}, #{reSeq})")
	int insert(CommentVo commentVo);
	
	@Select("SELECT * FROM comment WHERE cno = #{cno}")
	CommentVo getCommentByCno(int cno);
	
	@Delete("DELETE FROM comment WHERE cno = #{cno}")
	int deleteByCno(int cno);
	
	@Update("UPDATE comment "
			+ "SET content = #{content}, update_date = CURRENT_TIMESTAMP "
			+ "WHERE cno = #{cno}")
	void update(CommentVo commentVo);
	
	@Select("SELECT * "
			+ "FROM comment "
			+ "WHERE nno = #{nno} "
			+ "ORDER BY re_ref ASC, re_seq ASC ")
	List<CommentVo> getComments(int nno);
	
	@Select("SELECT * "
			+ "FROM comment "
			+ "WHERE nno = #{nno} "
			+ "ORDER BY re_ref ASC, re_seq ASC "
			+ "LIMIT #{cri.startRow}, #{cri.amount} ")
	List<CommentVo> getCommentsWithPaging(@Param("nno") int nno, @Param("cri") Criteria cri);
	
	@Update("UPDATE comment "
			+ "SET re_seq = re_seq + 1 "
			+ "WHERE re_ref = #{reRef} "
			+ "AND re_seq > #{reSeq} ")
	void updateReSeq(@Param("reRef") int reRef, @Param("reSeq") int reSeq);
	
}






