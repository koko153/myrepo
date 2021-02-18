package com.example.chat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class ChatRoom { // ä�ù�
	
	private String roomId; // ä�ù� ���̵�. �渶�� ������. UUID�� �ߺ����� ����.
	private String title;  // ä�ù� ����
	private Set<WebSocketSession> sessions; // ä�ù� �����ڸ� ������ Set �÷��� ��ü
	
	private static final Gson gson = new Gson();
	
	// ������
	public ChatRoom(String title) {
		this.title = title;
		this.roomId = UUID.randomUUID().toString();
		this.sessions = new HashSet<>();
	}
	
	public void handleMessage(WebSocketSession session, ChatMessage chatMessage) throws IOException {
		
		if (chatMessage.getType() == MessageType.ENTER) {
			sessions.add(session); // ������ ����� ������ �����(������) Set�� �߰��ϱ�
		} else if (chatMessage.getType() == MessageType.LEAVE) {
			sessions.remove(session); // ������ ����� ������ �����(������) Set���� �����ϱ�
		}
		
		send(chatMessage);
	} // handleMessage
	
	private void send(ChatMessage chatMessage) throws IOException {
		String strJson = gson.toJson(chatMessage);
		TextMessage textMessage = new TextMessage(strJson);
		
		for (WebSocketSession sess : sessions) {
			sess.sendMessage(textMessage);
		}
	} // send
}






