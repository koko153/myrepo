package com.example.domain;

import lombok.Data;

@Data
public class PageDto {
	
	private String category;
	private String search;
	private int count;
	private int pageCount;
	private int pageBlock;
	private int startPage;
	private int endPage;
}
