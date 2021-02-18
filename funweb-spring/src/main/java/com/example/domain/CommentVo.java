package com.example.domain;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Data

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentVo {
	
	private int cno;
	private int nno;
	private String id;
	private String content;
	private Timestamp regDate;
	private Timestamp updateDate;
	private int reRef;
	private int reLev;
	private int reSeq;
}



