package com.example.chat;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ChatTextWebSocketHandler extends TextWebSocketHandler {
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	@Autowired
	private Gson gson;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// HttpSession 에 있는 모든 속성값을 Map으로 가져오기
		Map<String, Object> attrMap = session.getAttributes();
		String loginId = (String) attrMap.get("id");
		log.info("loginId : {}", loginId);
		// =====================================================
		
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(MessageType.SESSION_ID);
		chatMessage.setSessionId(session.getId()); // 웹소켓 세션 아이디를 저장
		
		String strJson = gson.toJson(chatMessage);
		
		session.sendMessage(new TextMessage(strJson));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload();
		log.info("메시지 전송 = {} : {}", session, msg);
		
		ChatMessage chatMessage = gson.fromJson(msg, ChatMessage.class);
		
		String roomId = chatMessage.getRoomId();
		ChatRoom chatRoom = chatRoomRepository.getRoomById(roomId);
		
		chatRoom.handleMessage(session, chatMessage);
	} // handleTextMessage

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	}
}




