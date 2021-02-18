package com.example.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class MemberVo {
	
	private String id;
	private String passwd;
	private String name;
	private Integer age;
	private String gender;
	private String email;
	private Timestamp regDate;
	private String address;
	private String tel;
}




