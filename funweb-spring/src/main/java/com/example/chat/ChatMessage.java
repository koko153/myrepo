package com.example.chat;

import lombok.Data;

@Data
public class ChatMessage {
	
	private String roomId;
	private String sessionId;
	private String writer;
	private String message;
	//private String type;  // ä�ù� ����� "ENTER", ����� "LEAVE", ä�ý� "CHAT"
	private MessageType type;
}
