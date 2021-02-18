package com.example.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ChatRoomRepository { // DAO ����
	
	// DB ���̺� ����
	private Map<String, ChatRoom> chatRoomMap = new LinkedHashMap<>(); // Ű������ �������� ���ĵ�
	
	
	public List<ChatRoom> getAllRooms() {
		List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
		return chatRooms;
	}
	
	public ChatRoom getRoomById(String id) {
		ChatRoom chatRoom = chatRoomMap.get(id);
		return chatRoom;
	}
	
	public ChatRoom removeRoomById(String id) {
		ChatRoom chatRoom = chatRoomMap.remove(id);
		return chatRoom;
	}
	
	// ä�ù� �����ϰ� chatRoomMap�� �߰�
	public ChatRoom createChatRoom(String title) {
		ChatRoom chatRoom = new ChatRoom(title);
		chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
		return chatRoom;
	}
}
