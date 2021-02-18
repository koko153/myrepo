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
public class ChatRoom { // 채팅방
	
	private String roomId; // 채팅방 아이디. 방마다 생성됨. UUID로 중복없이 설정.
	private String title;  // 채팅방 제목
	private Set<WebSocketSession> sessions; // 채팅방 참가자를 저장할 Set 컬렉션 객체
	
	private static final Gson gson = new Gson();
	
	// 생성자
	public ChatRoom(String title) {
		this.title = title;
		this.roomId = UUID.randomUUID().toString();
		this.sessions = new HashSet<>();
	}
	
	public void handleMessage(WebSocketSession session, ChatMessage chatMessage) throws IOException {
		
		if (chatMessage.getType() == MessageType.ENTER) {
			sessions.add(session); // 입장한 사용자 세션을 현재방(참가방) Set에 추가하기
		} else if (chatMessage.getType() == MessageType.LEAVE) {
			sessions.remove(session); // 퇴장한 사용자 세션을 현재방(참가방) Set에서 제거하기
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






