package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

// ���� ���� ������ Ŭ����
@Component
@Slf4j
public class SimpleChatTextWebSocketHandler extends TextWebSocketHandler {
	
	private List<WebSocketSession> sessions = new ArrayList<>(); // ä�ù� ���� List �÷���

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("===== ������ Ŭ���̾�Ʈ�� ����� =====");
		
		sessions.add(session);
		log.info("���� : {}", session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.info("===== ������ Ŭ���̾�Ʈ�κ��� �����͸� ���� =====");
		
		String strMessage = message.getPayload();
		log.info("�޼��� ���� = {} : {}", session, strMessage);
		
		// ��ε�ĳ���� �ϱ�
		for (WebSocketSession sess : sessions) {
			TextMessage textMessage = new TextMessage(strMessage);
			sess.sendMessage(textMessage);
		} // for
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("===== ������ Ŭ���̾�Ʈ�� ������ ������ =====");
		
		sessions.remove(session);
		log.info("���� : {}", session);
	}
}



