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
		// "/simpleChat" ��δ� ���Ͽ����� ���� ws ������ �������� ���� ��ΰ� ��!
		// ws �Ǵ� wss ���������� �̿��� �Ʒ� ��η� ���ϼ����� �����ؾ� �����.
		registry.addHandler(simpleChatHandler, "/simpleChat")
				.addHandler(chatHandler, "/chat")
				// HttpSessionHandshakeInterceptor ��  HttpSession�� �ִ� �Ӽ������� �ش� WebSocketSession�� Map ��ü�� ��������
				.addInterceptors(new HttpSessionHandshakeInterceptor())
				.setAllowedOrigins("*"); // ���� �������� ���� ��� ��� (���� ������ ȣȯ�� ����)
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




