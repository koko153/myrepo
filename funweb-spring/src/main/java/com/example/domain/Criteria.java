package com.example.domain;

import lombok.Data;

@Data
public class Criteria {
	
	private int pageNum;  // 사용자 요청한 댓글 페이지 번호
	private int amount;   // 한페이지당 보여줄 댓글 갯수
	private int startRow; // 가져올 댓글 레코드의 시작 행번호
	
	public Criteria() {
		this(1, 10);  // 기본동작은 1페이지에 10개씩 댓글 가져오도록 설정
	}
	
	public Criteria(int pageNum, int amount) {
		this.pageNum = pageNum;
		this.amount = amount;
		
		// MySQL 기준: 시작 행번호가 0부터 시작함
		this.startRow = (this.pageNum - 1) * this.amount;
		
		// Oracle 기준: 시작 행번호가 1부터 시작함
//		this.startRow = (this.pageNum - 1) * this.amount + 1;
	}
	

}
