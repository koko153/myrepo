package com.example.domain;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class NoticeVo {
	
	private int num;
	private String id;
	private String subject;
	private String content;
	private int readcount;
	private Timestamp regDate;
	private String ip;
	private int reRef;
	private int reLev;
	private int reSeq;
	
	//private AttachVo attachVo;        // JOIN 孽府 1:1 包拌
	private List<AttachVo> attachList;  // JOIN 孽府 1:N 包拌
}