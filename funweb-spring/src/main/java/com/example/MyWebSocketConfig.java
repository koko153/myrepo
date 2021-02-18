package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.example.chat.ChatTextWebSocketHandler;
import com.example.chat.SimpleChatTextWebSocketHandler;


@Configuration
public class MyWebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	private SimpleChatTextWebSocketHandler simpleChatHandler;
	@Autowired
	private ChatTextWebSocketHandler chatHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// "/simpleChat" 경로는 소켓연결을 위한 ws 웹소켓 프로토콜 연결 경로가 됨!
		// ws 또는 wss 프로토콜을 이용해 아래 경로로 소켓서버에 접속해야 연결됨.
		registry.addHandler(simpleChatHandler, "/simpleChat")
				.addHandler(chatHandler, "/chat")
				// HttpSessionHandshakeInterceptor 는  HttpSession에 있는 속성값들을 해당 WebSocketSession에 Map 객체로 복사해줌
				.addInterceptors(new HttpSessionHandshakeInterceptor())
				.setAllowedOrigins("*"); // 접속 프로토콜 종류 모두 허용 (옛날 브라우저 호환성 때문)
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(2);
		taskScheduler.setThreadNamePrefix("scheduled-task-");
		taskScheduler.setDaemon(true);
		return taskScheduler;
	}

}




