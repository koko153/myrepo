package com.example.chat;

import lombok.Data;

@Data
public class ChatMessage {
	
	private String roomId;
	private String sessionId;
	private String writer;
	private String message;
	//private String type;  // 채팅방 입장시 "ENTER", 퇴장시 "LEAVE", 채팅시 "CHAT"
	private MessageType type;
}
