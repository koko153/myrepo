package com.example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j // log 준비
class FunwebSpringApplicationTests {
	
//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	@Test
//	void testBCrypt1() {
//		// 암호화 할 패스워드
//		String inputPwd = "abcd1234";
//		String encodedPwd = bCryptPasswordEncoder.encode(inputPwd);
//		log.info("encodedPwd : {}", encodedPwd);
//		
//		boolean isMatched = bCryptPasswordEncoder.matches(inputPwd, encodedPwd);
//		log.info("isMatched : {}", isMatched);
//	}
	
	@Test
	void testBCrypt2() {
		// 암호화 할 패스워드
		String inputPwd = "abcd1234";
		String hashedPwd = BCrypt.hashpw(inputPwd, BCrypt.gensalt());
		log.info("hashedPwd : {}", hashedPwd);
		
		boolean isMatched = BCrypt.checkpw(inputPwd, hashedPwd);
		log.info("isMatched : {}", isMatched);
	}
	
	@Disabled
	@Test
	void contextLoads() {
	}

}
