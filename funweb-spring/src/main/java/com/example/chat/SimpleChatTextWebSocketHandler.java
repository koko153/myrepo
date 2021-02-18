package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

// 소켓 서버 역할의 클래스
@Component
@Slf4j
public class SimpleChatTextWebSocketHandler extends TextWebSocketHandler {
	
	private List<WebSocketSession> sessions = new ArrayList<>(); // 채팅방 역할 List 컬렉션

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("===== 웹소켓 클라이언트와 연결됨 =====");
		
		sessions.add(session);
		log.info("접속 : {}", session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.info("===== 웹소켓 클라이언트로부터 데이터를 받음 =====");
		
		String strMessage = message.getPayload();
		log.info("메세지 전송 = {} : {}", session, strMessage);
		
		// 브로드캐스팅 하기
		for (WebSocketSession sess : sessions) {
			TextMessage textMessage = new TextMessage(strMessage);
			sess.sendMessage(textMessage);
		} // for
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("===== 웹소켓 클라이언트와 연결이 해제됨 =====");
		
		sessions.remove(session);
		log.info("퇴장 : {}", session);
	}
}



